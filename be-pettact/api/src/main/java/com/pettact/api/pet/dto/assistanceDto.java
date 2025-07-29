package com.pettact.api.pet.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class assistanceDto {

    private String message;
    private Long userNo;
    private Long sessionNo;
    private String petType;
    private Long petId;
}
