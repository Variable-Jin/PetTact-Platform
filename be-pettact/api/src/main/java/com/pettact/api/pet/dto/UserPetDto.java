package com.pettact.api.pet.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserPetDto {

    private Long petId;         // 반려동물 고유 번호 (PK)

    private Long userNo;        // 사용자 고유 번호 (Users.userNo)

    private String kindCd;      // 품종 코드 (PetKindEntity.kindCd)
    
    private String kindNm;      // 품종 코드 (PetKindEntity.kindCd)

    private String rfidNo;      // RFID 번호

    private String petName;     // 반려동물 이름

    private String petGender;   // 성별 ("M", "F")

    private String isNeutered;  // 중성화 여부 ("Y", "N")

    private LocalDate petBirth; // 반려동물 생년월일

    private Float petWeight;    // 몸무게 (kg)

    private String petImageUrl; // 반려동물 사진 URL

    private LocalDateTime createdAt;  // 등록일

    private String OwnerRelation;
}
