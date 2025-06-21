package com.pettact.api.board.Category;


import com.pettact.api.board.Category.dto.CreateDto;
import com.pettact.api.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BoardCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_category_no")
    private Long boardCategoryNo;

    @Column(name = "boaard_category_title", nullable = false, length = 255)
    private String boardCategoryTitle;

    @Column(name = "board_category_description", columnDefinition = "TEXT")
    private String boardCategoryDescription;

    // enum 변경 예정 Boolean -> String
    @Column(name = "board_allow_role", nullable = false)
    private String boardAllowedRole;

    @Column(name = "board_allow_image", nullable = false)
    private Boolean boardAllowImage;

    @Column(name = "board_allow_attachment", nullable = false)
    private Boolean boardAllowAttachment;

    @Column(name = "board_max_file_size", nullable = false)
    private Integer boardMaxFileSize;

    @Column(name = "board_allow_reply", nullable = false)
    private Boolean boardAllowReply;

    @Column(name = "board_max_image_count")
    private Integer boardMaxImageCount;

    @Column(name = "editor_type", nullable = false)
    private String editorType;

    @Column(name = "board_allow_recommend", nullable = false)
    private Boolean boardAllowRecommend;

    public BoardCategory(String boardCategoryTitle, String boardCategoryDescription, String boardAllowedRole, Boolean boardAllowAttachment,
                         Boolean boardAllowImage, Boolean boardAllowReply, Boolean boardAllowRecommend, Integer boardMaxFileSize, Integer boardMaxImageCount, String editorType) {
        super();
        this.boardCategoryTitle = boardCategoryTitle;
        this.boardCategoryDescription = boardCategoryDescription;
        this.boardAllowedRole = boardAllowedRole;
        this.boardAllowAttachment = boardAllowAttachment;
        this.boardAllowImage = boardAllowImage;
        this.boardAllowReply = boardAllowReply;
        this.boardAllowRecommend = boardAllowRecommend;
        this.boardMaxFileSize = boardMaxFileSize;
        this.boardMaxImageCount = boardMaxImageCount;
        this.editorType = editorType;
    }

    public void updateCategoryFiled(CreateDto createDto) {
        this.boardCategoryTitle = createDto.getBoardCategoryTitle();
        this.boardCategoryDescription = createDto.getBoardCategoryDescription();
        this.boardAllowImage = createDto.getBoardAllowImage();
        this.boardAllowAttachment = createDto.getBoardAllowAttachment();
        this.boardMaxFileSize = createDto.getBoardMaxFileSize();
        this.boardAllowReply = createDto.getBoardAllowReply();
        this.boardMaxImageCount = createDto.getBoardMaxImageCount();
        this.editorType = createDto.getEditorType();
        this.boardAllowRecommend = createDto.getBoardAllowRecommend();
    }
}
