package com.pettact.api.Category.dto;


import com.pettact.api.Category.entity.BoardCategory;
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
    private int totalBoards;
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
                0,
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }
}
