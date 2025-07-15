package com.pettact.api.multiFile.entity;


import com.pettact.api.core.base.BaseEntity;
import com.pettact.api.multiFile.dto.FileUpdateDto;
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
@ToString
public class MultiFile extends BaseEntity {

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
    
    @Column(name = "image_url")
    private String imageUrl;

    public MultiFile(ReferenceTable referenceTable, Long referenceNo) {
        super();
        this.referenceTable = referenceTable;
        this.referenceNo = referenceNo;
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


    public MultiFile(ReferenceTable referenceTable, Long referenceNo, String fileName) {
        super();
        this.referenceTable = referenceTable;
        this.referenceNo = referenceNo;
        this.fileName = fileName;
    }

    public void patch(FileUpdateDto fileUpdateDto) {
        if (fileUpdateDto.getFileName() != null) {
            this.fileName = fileUpdateDto.getFileName();
        }
        if (fileUpdateDto.getFileSize() != null) {
            this.fileSize = fileUpdateDto.getFileSize();
        }
        if (fileUpdateDto.getFileOrderNo() != null) {
            this.fileOrderNo = fileUpdateDto.getFileOrderNo();
        }
        if (fileUpdateDto.getFileMimeType() != null) {
            this.fileMimeType = fileUpdateDto.getFileMimeType();
        }
    }
}
