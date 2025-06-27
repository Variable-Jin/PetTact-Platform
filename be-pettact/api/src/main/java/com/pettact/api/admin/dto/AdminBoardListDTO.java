package com.pettact.api.admin.dto;

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
    private String writer;
    private LocalDateTime createdAt;
    private boolean isDeleted;
    
    public static AdminBoardListDTO from(Board board) {
        return AdminBoardListDTO.builder()
            .boardNo(board.getBoardNo())
            .boardTitle(board.getBoardTitle())
            .categoryName(board.getBoardCategory().getBoardCategoryTitle())
            .writer(board.getUser().getUserNickname())
            .createdAt(board.getCreatedAt())
            .isDeleted(board.isDeleted())
            .build();
    }

}

