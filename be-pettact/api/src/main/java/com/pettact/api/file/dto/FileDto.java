package com.pettact.api.file.dto;


import com.pettact.api.file.entity.File;
import com.pettact.api.multiFile.entity.MultiFile;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FileDto {

    private Long fileNo;
    private Long userNo;
    private File.ReferenceTable referenceTable;
    private Long referenceNo;
    private String fileName;
    private String storedFileName;
    private Integer fileSize;
    private Integer fileOrderNo;
    private String filePath;
    private String fileMimeType;
    private String imageURL;


    public static FileDto fromEntity(File file) {
        return new FileDto(
                file.getFileNo(),
                file.getUserNo(),
                file.getReferenceTable(),
                file.getReferenceNo(),
                file.getFileName(),
                file.getStoredFileName(),
                file.getFileSize(),
                file.getFileOrderNo(),
                file.getFilePath(),
                file.getFileMimeType(),
                file.getImageURL()
        );
    }
}
