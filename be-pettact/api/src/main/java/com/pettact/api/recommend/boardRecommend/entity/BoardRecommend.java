package com.pettact.api.recommend.boardRecommend.entity;


import com.pettact.api.board.entity.Board;
import com.pettact.api.core.base.BaseEntity;
import com.pettact.api.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"board", "users"})
public class BoardRecommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardRecommendNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_no")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private Users users;

    private LocalDateTime createdAt;


    public BoardRecommend(Board board, Users users, LocalDateTime createdAt) {
        this.board = board;
        this.users = users;
        this.createdAt = createdAt;
    }
}
