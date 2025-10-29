package com.pettact.api.reply.service;


import com.pettact.api.board.entity.Board;
import com.pettact.api.board.repository.BoardRepository;
import com.pettact.api.notification.dto.NotificationReqDTO;
import com.pettact.api.notification.enums.NotificationType;
import com.pettact.api.notification.enums.TargetType;
import com.pettact.api.notification.service.NotificationService;
import com.pettact.api.recommend.replyRecommend.repository.ReplyRecommendRepository;
import com.pettact.api.reply.dto.ReplyRequestDto;
import com.pettact.api.reply.dto.ReplyResponseDto;
import com.pettact.api.reply.entity.Reply;
import com.pettact.api.reply.repository.ReplyRepository;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReplyService {
    
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReplyRecommendRepository replyRecommendRepository;
    @Autowired
    private NotificationService notificationService;

    @Transactional
    public ReplyResponseDto createReply(Long boardNo, ReplyRequestDto replyRequestDto, Long userNo) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(()-> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Users users = userRepository.findById(userNo)
                .orElseThrow(()-> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        Reply parentReply = null;
        if (replyRequestDto.getParentReplyNo() != null) {
            parentReply = replyRepository.findById(replyRequestDto.getParentReplyNo())
                    .orElseThrow(()-> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
        }
        // DTO의 userNo를 인증된 사용자로 덮어쓰기!
        replyRequestDto.setUserNo(userNo);

        Reply reply = replyRequestDto.toEntity(board, parentReply, users);
        Reply savedReply = replyRepository.save(reply);
        
        sendCommentNotification(board, parentReply, users, reply);
        
        return ReplyResponseDto.fromEntity(savedReply);
    }

    private void sendCommentNotification(Board board, Reply parentReply, Users commenter, Reply reply) {
        Long userNo = commenter.getUserNo();

        if (parentReply != null) {
            Long parentUserNo = parentReply.getUser().getUserNo();
            Long boardOwnerNo = board.getUser().getUserNo();

            if (!parentUserNo.equals(userNo)) {
                NotificationReqDTO dto = NotificationReqDTO.of(
                    userNo, parentUserNo,
                    NotificationType.COMMENT,
                    board.getBoardNo(), TargetType.BOARD,
                    "내 댓글에 대댓글이 달렸습니다.",
                    commenter.getUserNickname() + "님이 대댓글을 남겼습니다: " + reply.getContent()
                );
                notificationService.sendNotification(dto);
            }

            if (!boardOwnerNo.equals(parentUserNo) && !boardOwnerNo.equals(userNo)) {
                NotificationReqDTO dto = NotificationReqDTO.of(
                    userNo, boardOwnerNo,
                    NotificationType.COMMENT,
                    board.getBoardNo(), TargetType.BOARD,
                    "새 댓글이 달렸습니다.",
                    commenter.getUserNickname() + "님이 댓글을 남겼습니다: " + reply.getContent()
                );
                notificationService.sendNotification(dto);
            }

        } else {
            Long boardOwnerNo = board.getUser().getUserNo();
            if (!boardOwnerNo.equals(userNo)) {
                NotificationReqDTO dto = NotificationReqDTO.of(
                    userNo, boardOwnerNo,
                    NotificationType.COMMENT,
                    board.getBoardNo(), TargetType.BOARD,
                    "새 댓글이 달렸습니다.",
                    commenter.getUserNickname() + "님이 댓글을 남겼습니다: " + reply.getContent()
                );
                notificationService.sendNotification(dto);
            }
        }
    }


    public List<ReplyResponseDto> getAllReplies(Long boardNo) {
        boardRepository.findById(boardNo)
                .orElseThrow(()-> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        List<Reply> replies = replyRepository.findRepliesByBoardNo(boardNo);
        return buildHierarchy(replies);
    }

    /**
     * 댓글 리스트를 계층구조로 변환
     */
    private List<ReplyResponseDto> buildHierarchy(List<Reply> replies) {
        Map<Long, ReplyResponseDto> replyMap = new HashMap<>();
        List<ReplyResponseDto> rootReplies = new ArrayList<>();

        // 모든 댓글을 DTO로 변환하여 Map에 저장
        for (Reply reply : replies) {
            ReplyResponseDto dto = ReplyResponseDto.fromEntity(reply);

            // 댓글 추천수 설정 추가!
            int recommendCount = replyRecommendRepository.countByReplyNo(reply.getReplyNo());
            dto.setRecommendCount(recommendCount);

            replyMap.put(reply.getReplyNo(), dto);
        }

        // 계층구조 구성
        for (Reply reply : replies) {
            ReplyResponseDto dto = replyMap.get(reply.getReplyNo());

            if (reply.getParentReply() == null) {
                // 최상위 댓글 (부모가 없는 댓글)
                rootReplies.add(dto);
            } else {
                // 대댓글 (부모 댓글의 childReplies에 추가)
                Long parentId = reply.getParentReply().getReplyNo();
                ReplyResponseDto parent = replyMap.get(parentId);
                if (parent != null) {
                    parent.getChildReplies().add(dto);
                }
            }
        }

        return rootReplies;
    }

    @Transactional
    public ReplyResponseDto updateReply(Long replyNo, ReplyRequestDto replyRequestDto, Long userNo) {
        Reply reply = replyRepository.findById(replyNo)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!reply.getUser().getUserNo().equals(userNo)) {
            throw new IllegalArgumentException("댓글 작성자만 수정할 수 있습니다.");
        }
        reply.updateContent(replyRequestDto);
        Reply updatedReply = replyRepository.save(reply);
        return ReplyResponseDto.fromEntity(updatedReply);
    }


    public void deleteReply(Long replyNo, Long userNo) {
        Reply reply = replyRepository.findById(replyNo)
                .orElseThrow(()-> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!reply.getUser().getUserNo().equals(userNo)) {
            throw new IllegalArgumentException("댓글 작성자만 삭제할 수 있습니다.");
        }
        boolean hasChildReplies = replyRepository.existsByParentReply_ReplyNo(replyNo);

        if (hasChildReplies) {
            // 대댓글이 있으면 구조 유지(soft delete)
            reply.markAsDeleted();
        } else {
            // 대댓글 x -> 완전 삭제
            replyRepository.delete(reply);
        }
    }
}
