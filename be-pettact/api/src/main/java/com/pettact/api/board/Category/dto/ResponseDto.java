package com.pettact.api.board.Category.dto;


import com.pettact.api.board.Category.BoardCategory;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResponseDto {

    private Long boardCategoryNo;
    private String boardCategoryTitle;
    private String boardCategoryDescription;
    private String boardAllowedRole;
    private Boolean boardAllowImage;
    private Boolean boardAllowAttachment;
    private Boolean boardAllowReply;
    private Boolean boardAllowRecommend;
    private Integer boardMaxFileSize;
    private Integer boardMaxImageCount;
    private String editorType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public static ResponseDto fromEntity(BoardCategory saved) {
        return new ResponseDto(
                saved.getBoardCategoryNo(),
                saved.getBoardCategoryTitle(),
                saved.getBoardCategoryDescription(),
                saved.getBoardAllowedRole(),
                saved.getBoardAllowImage(),
                saved.getBoardAllowAttachment(),
                saved.getBoardAllowReply(),
                saved.getBoardAllowRecommend(),
                saved.getBoardMaxFileSize(),
                saved.getBoardMaxImageCount(),
                saved.getEditorType(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }
}
