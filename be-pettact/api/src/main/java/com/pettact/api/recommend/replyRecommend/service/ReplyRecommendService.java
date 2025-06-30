package com.pettact.api.recommend.replyRecommend.service;


import com.pettact.api.recommend.boardRecommend.entity.BoardRecommend;
import com.pettact.api.recommend.replyRecommend.dto.ReplyRecommendDto;
import com.pettact.api.recommend.replyRecommend.entity.ReplyRecommend;
import com.pettact.api.recommend.replyRecommend.repository.ReplyRecommendRepository;
import com.pettact.api.reply.entity.Reply;
import com.pettact.api.reply.repository.ReplyRepository;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ReplyRecommendService {

    @Autowired
    private ReplyRecommendRepository replyRecommendRepository;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void createRecommend(Long replyNo, Long userNo) {
        Reply reply = replyRepository.findById(replyNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        Users users = userRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        if (replyRecommendRepository.existsByReply_ReplyNoAndUsers_UserNo(replyNo, userNo)) {
            throw new IllegalArgumentException("이미 추천한 댓글입니다.");
        }
        ReplyRecommend replyRecommend = new ReplyRecommend(
                reply,
                users,
                LocalDateTime.now()
        );
        replyRecommendRepository.save(replyRecommend);
    }

    @Transactional
    public void cancelRecommend(Long replyNo, Long userNo) {
        ReplyRecommend replyRecommend = replyRecommendRepository
                .findByReplyAndUser(replyNo, userNo)
                .orElseThrow(() -> new IllegalArgumentException("추천하지 않은 댓글입니다."));
        replyRecommendRepository.delete(replyRecommend);
    }
}
