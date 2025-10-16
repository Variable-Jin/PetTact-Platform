package com.pettact.api.file.entity;

import com.pettact.api.core.base.BaseEntity;
import com.pettact.api.file.dto.UpdateFileDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FILE_NO")
    private Long fileNo;

    @Column(name = "user_no")
    private Long userNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "REFERENCE_TABLE")
    private ReferenceTable referenceTable;

    @Column(name = "REFERENCE_NO", nullable = false)
    private Long referenceNo;

    @Column(name = "FILE_NAME", nullable = false)
    private String fileName;

    @Column(name = "STORED_FILE_NAME", nullable = false)
    private String storedFileName;  // 실제 서버에 저장된 파일명 (예: 1751775558286_1sample.jpg)

    @Column(name = "FILE_SIZE", nullable = false)
    private Integer fileSize;

    @Column(name = "FILE_ORDER_NO")
    private Integer fileOrderNo;

    @Column(name = "FILE_PATH")
    private String filePath;

    @Column(name = "FILE_MIME_TYPE")
    private String fileMimeType;

    @Column(name = "IMAGE_URL")
    private String imageURL;

    public void patch(UpdateFileDto updateFileDto) {
        if (updateFileDto.getFileName() != null) {
            this.fileName = updateFileDto.getFileName();
        }
        if (updateFileDto.getFileOrderNo() != null) {
            this.fileOrderNo = updateFileDto.getFileOrderNo();
        }
    }


    public enum ReferenceTable {
        BOARD,
        REPLY,
        PRODUCT,
        CART,
        ORDER,
        PET,
        USER;
    }
}

