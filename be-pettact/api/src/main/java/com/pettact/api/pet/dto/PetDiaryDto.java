package com.pettact.api.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetDiaryDto {
    private Long diaryId;
    private Long userNo;
    private Long petId;
    private String prompt; 
    private String diaryContent;
}
