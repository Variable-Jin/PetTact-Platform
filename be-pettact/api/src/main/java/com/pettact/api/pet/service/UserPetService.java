package com.pettact.api.pet.service;

import com.pettact.api.pet.dto.UserPetDto;
import com.pettact.api.pet.entity.PetKindEntity;
import com.pettact.api.pet.entity.UserPetEntity;
import com.pettact.api.pet.repository.PetKindRepository;
import com.pettact.api.pet.repository.UserPetRepository;
import com.pettact.api.user.entity.Users;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserPetService {

    private final UserPetRepository userPetRepository;
    private final PetKindRepository petKindRepository;
    
    // 등록
    public UserPetDto save(UserPetDto dto) {
    	
        PetKindEntity petKind = petKindRepository.findById(dto.getKindCd())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 품종 코드입니다."));
    	
        UserPetEntity entity = UserPetEntity.builder()
            .user(Users.builder().userNo(dto.getUserNo()).build())
            .petKind(PetKindEntity.builder().kindCd(dto.getKindCd()).build())
            .rfidNo(dto.getRfidNo())
            .petName(dto.getPetName())
            .petGender(dto.getPetGender())
            .isNeutered(dto.getIsNeutered())
            .petBirth(dto.getPetBirth())
            .petWeight(dto.getPetWeight())
            .petImageUrl(dto.getPetImageUrl())
            .specialNotes(dto.getSpecialNotes())
            .build();

        UserPetEntity saved = userPetRepository.save(entity);
        return toDto(saved);
    }

    // 단건 조회
    public UserPetDto detailPet(Long petId) {
        UserPetEntity entity = userPetRepository.findById(petId)
            .filter(pet -> !pet.isDeleted())
            .orElseThrow(() -> new IllegalArgumentException("해당 반려동물이 존재하지 않습니다."));
        return toDto(entity);
    }

    // 사용자별 전체 조회
    public Page<UserPetDto> petList(Long userNo, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<UserPetEntity> result = userPetRepository.findByUser_UserNoAndIsDeletedFalse(userNo, pageable);
        return result.map(this::toDto); 
    }


    // 논리 삭제 (soft delete)
    public void petDelete(Long petId) {
        UserPetEntity entity = userPetRepository.findById(petId)
            .orElseThrow(() -> new IllegalArgumentException("삭제할 반려동물이 존재하지 않습니다."));
        entity.softDelete(); // isDeleted = true, deletedAt 세팅
        userPetRepository.save(entity); // 상태 저장
    }

    private UserPetDto toDto(UserPetEntity entity) {
        UserPetDto dto = new UserPetDto();
        if (entity.getPetKind() != null) {
            dto.setKindNm(entity.getPetKind().getKindNm());
        }
        dto.setPetId(entity.getPetId());
        dto.setUserNo(entity.getUser().getUserNo());
        dto.setKindCd(entity.getPetKind().getKindCd());
        dto.setRfidNo(entity.getRfidNo());
        dto.setPetName(entity.getPetName());
        dto.setPetGender(entity.getPetGender());
        dto.setIsNeutered(entity.getIsNeutered());
        dto.setPetBirth(entity.getPetBirth());
        dto.setPetWeight(entity.getPetWeight());
        dto.setPetImageUrl(entity.getPetImageUrl());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    @Transactional
    public UserPetDto update(UserPetDto dto) throws AccessDeniedException {
        UserPetEntity pet = userPetRepository.findById(dto.getPetId())
            .orElseThrow(() -> new EntityNotFoundException("해당 반려동물이 존재하지 않습니다."));

        // 자신이 등록한 반려동물인지 검증 
        if (!pet.getUser().getUserNo().equals(dto.getUserNo())) {
            throw new AccessDeniedException("해당 반려동물에 대한 수정 권한이 없습니다.");
        }

        // 수정 가능한 필드들만 업데이트
        pet.setPetName(dto.getPetName());
        pet.setPetWeight(dto.getPetWeight());
        pet.setPetImageUrl(dto.getPetImageUrl());
        pet.setPetGender(dto.getPetGender());
        pet.setIsNeutered(dto.getIsNeutered());
        pet.setPetBirth(dto.getPetBirth());
        pet.setRfidNo(dto.getRfidNo());

        if (dto.getKindCd() != null) {
            PetKindEntity kind = petKindRepository.findById(dto.getKindCd())
                .orElseThrow(() -> new EntityNotFoundException("해당 품종 정보 없음"));
            pet.setPetKind(kind);
        }
        userPetRepository.save(pet);
        return toDto(pet);
    }

}
