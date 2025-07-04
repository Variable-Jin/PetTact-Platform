package com.pettact.api.multiFile.dto;


import com.pettact.api.multiFile.entity.MultiFile;
import com.pettact.api.user.entity.Users;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FileCreateDto {

    private MultiFile.ReferenceTable referenceTable;
    private Long referenceNo;
    private String fileName;

    public MultiFile toEntity() {
        return new MultiFile(
          referenceTable,
          referenceNo,
          fileName
        );
    }
}
