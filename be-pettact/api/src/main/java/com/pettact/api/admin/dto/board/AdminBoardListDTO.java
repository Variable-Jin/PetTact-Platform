package com.pettact.api.admin.dto.board;

import java.time.LocalDateTime;

import com.pettact.api.board.entity.Board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AdminBoardListDTO {
    private Long boardNo;
    private String boardTitle;
    private String categoryName;
    private Long userNo;
    private String userEmail;
    private String userNickname;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private boolean isDeleted;
    
    public static AdminBoardListDTO from(Board board) {
        return AdminBoardListDTO.builder()
                .boardNo(board.getBoardNo())
                .boardTitle(board.getBoardTitle())
                .categoryName(board.getBoardCategory().getBoardCategoryTitle())
                .userNo(board.getUser().getUserNo())
                .userEmail(board.getUser().getUserEmail())
                .userNickname(board.getUser().getUserNickname())
                .createdAt(board.getCreatedAt())
                .deletedAt(board.getDeletedAt())
                .isDeleted(board.isDeleted())
                .build();
    }

}

