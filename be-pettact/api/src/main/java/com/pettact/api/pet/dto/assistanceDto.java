package com.pettact.api.pet.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class assistanceDto {

    private String message;
    private Long userNo;
    private Long sessionNo;
}
