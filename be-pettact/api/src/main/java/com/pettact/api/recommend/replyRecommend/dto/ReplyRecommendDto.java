package com.pettact.api.recommend.replyRecommend.dto;

import com.pettact.api.recommend.replyRecommend.entity.ReplyRecommend;
import com.pettact.api.reply.entity.Reply;
import com.pettact.api.user.entity.Users;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReplyRecommendDto {

    private Long replyRecommendNo;

    private Long replyNo;

    private Long userNo;

    private LocalDateTime createdAt;

    public ReplyRecommend toEntity(Reply reply, Users users) {
        return new ReplyRecommend(
                reply,
                users,
                LocalDateTime.now()
        );
    }
}
