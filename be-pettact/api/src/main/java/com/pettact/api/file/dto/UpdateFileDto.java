package com.pettact.api.file.dto;


import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdateFileDto {

    private String fileName; 
    private Integer fileOrderNo;

}
