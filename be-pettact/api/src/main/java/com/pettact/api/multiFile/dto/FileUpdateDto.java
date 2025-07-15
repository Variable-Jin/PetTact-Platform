package com.pettact.api.multiFile.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileUpdateDto {

    private String fileName;
    private Integer fileSize;
    private Integer fileOrderNo;
    private String fileMimeType;
}
