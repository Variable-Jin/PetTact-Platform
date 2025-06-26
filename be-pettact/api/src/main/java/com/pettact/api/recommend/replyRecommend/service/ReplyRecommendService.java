package com.pettact.api.recommend.replyRecommend.service;


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

@Service
public class ReplyRecommendService {

    @Autowired
    private ReplyRecommendRepository replyRecommendRepository;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void createRecommend(Long replyNo, ReplyRecommendDto replyRecommendDto) {

        if (!replyNo.equals(replyRecommendDto.getReplyNo())) {
            throw new IllegalArgumentException("댓글 번호가 일치하지 않습니다.");
        }
        if (replyRecommendRepository.existsByReply_ReplyNoAndUsers_UserNo(
                replyRecommendDto.getReplyNo(),
                replyRecommendDto.getUserNo())) {
            throw new IllegalArgumentException("이미 추천한 댓글입니다.");
        }

        Reply reply = replyRepository.findById(replyRecommendDto.getReplyNo())
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        Users users = userRepository.findById(replyRecommendDto.getUserNo())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        ReplyRecommend replyRecommend = replyRecommendDto.toEntity(reply, users);
        replyRecommendRepository.save(replyRecommend);
    }

    @Transactional
    public void cancelRecommend(Long replyNo, ReplyRecommendDto replyRecommendDto) {
        if (!replyNo.equals(replyRecommendDto.getReplyNo())) {
            throw new IllegalArgumentException("댓글 번호가 일치하지 않습니다.");
        }

        ReplyRecommend replyRecommend = replyRecommendRepository
                .findByReplyAndUser(replyRecommendDto.getReplyNo(), replyRecommendDto.getUserNo())
                .orElseThrow(() -> new IllegalArgumentException("추천하지 않은 댓글입니다."));
        replyRecommendRepository.delete(replyRecommend);
    }
}
