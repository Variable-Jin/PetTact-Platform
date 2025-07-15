package com.pettact.api.recommend.replyRecommend.entity;

import com.pettact.api.reply.entity.Reply;
import com.pettact.api.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"reply", "users"})
public class ReplyRecommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyRecommendNo;

    @ManyToOne
    @JoinColumn(name = "reply_no")
    private Reply reply;

    @ManyToOne
    @JoinColumn(name = "user_no")
    private Users users;

    private LocalDateTime createdAt;

    public ReplyRecommend(Reply reply, Users users, LocalDateTime createdAt) {
        this.reply = reply;
        this.users = users;
        this.createdAt = createdAt;
    }

}
