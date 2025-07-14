package com.pettact.api.admin.dto.board;

import java.time.LocalDateTime;

import com.pettact.api.board.entity.Board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AdminBoardDetailDTO {
    private Long boardNo;
    private String boardTitle;
    private String boardContent;
    private String categoryName;
    private String writer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;

    public static AdminBoardDetailDTO from(Board board) {
        return AdminBoardDetailDTO.builder()
                .boardNo(board.getBoardNo())
                .boardTitle(board.getBoardTitle())
                .boardContent(board.getBoardContent())
                .categoryName(board.getBoardCategory().getBoardCategoryTitle())
                .writer(board.getUser().getUserNickname())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .isDeleted(board.isDeleted())
                .build();
    }
}
