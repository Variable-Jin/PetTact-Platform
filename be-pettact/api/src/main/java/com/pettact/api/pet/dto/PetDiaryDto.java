package com.pettact.api.pet.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetDiaryDto {
    private Long petId;
    private Long diaryId;
    private String prompt; 
    private String diaryContent;
    private LocalDateTime createdAt;
}
