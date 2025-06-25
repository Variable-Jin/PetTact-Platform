package com.pettact.api.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetSigunguDto {
    private String orgCd;
    private String orgdownNm;
    private String uprCd;
}