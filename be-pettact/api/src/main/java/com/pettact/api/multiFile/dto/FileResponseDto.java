package com.pettact.api.multiFile.dto;


import com.pettact.api.multiFile.entity.MultiFile;
import com.pettact.api.reply.entity.Reply;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FileResponseDto {

    private Long fileNo;
    private MultiFile.ReferenceTable referenceTable;
    private Long referenceNo;
    private String fileName;
    private Integer fileSize;
    private Integer fileOrderNo;
    private String filePath;
    private String fileMimeType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String imageUrl;
    
    private String storedFileName; // 저장된 파일 이름


    public static FileResponseDto fromEntity(MultiFile saved) {
        return new FileResponseDto(
                saved.getFileNo(),
                saved.getReferenceTable(),
                saved.getReferenceNo(),
                saved.getFileName(),
                saved.getFileSize(),
                saved.getFileOrderNo(),
                saved.getFilePath(),
                saved.getFileMimeType(),
                saved.getCreatedAt(),
                saved.getUpdatedAt(),
                saved.getImageUrl(),
                saved.getStoredFileName()   // ✅ 추가
        );
    }
}
