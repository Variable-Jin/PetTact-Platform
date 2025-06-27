package com.pettact.api.recommend.boardRecommend.dto;

import com.pettact.api.board.entity.Board;
import com.pettact.api.recommend.boardRecommend.entity.BoardRecommend;
import com.pettact.api.user.entity.Users;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BoardRecommendDto {

    private Long boardRecommendNo;

    private Long boardNo;
    private String boardTitle;

    private Long userNo;
    private String userNickname;

    private LocalDateTime createdAt;

}
