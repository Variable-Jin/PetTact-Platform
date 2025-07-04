package com.pettact.api.pet.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.pettact.api.pet.dto.PetAbandonmentDto;
import com.pettact.api.pet.dto.PetOriginFacilityDto;
import com.pettact.api.pet.dto.PetShelterDto;
import com.pettact.api.pet.entity.PetAbandonmentEntity;
import com.pettact.api.pet.entity.PetFacilityEntity;
import com.pettact.api.pet.entity.PetShelterEntity;
import com.pettact.api.pet.repository.PetAbandonmentRepository;
import com.pettact.api.pet.repository.PetFacilityRepository;
import com.pettact.api.pet.repository.PetShelterRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetAdminService {

    private final PetAbandonmentRepository abandonmentRepository;
    private final PetFacilityRepository facilityRepository;
    private final PetShelterRepository shelterRepository;
    
    private final ModelMapper mapper;

    // ------------------ 유기동물 ------------------

    public PetAbandonmentDto detailAbandonment(String desertionNo) {
        PetAbandonmentEntity entity = abandonmentRepository.findByDesertionNo(desertionNo)
            .orElseThrow(() -> new EntityNotFoundException("유기동물 정보 없음"));
        return mapper.map(entity, PetAbandonmentDto.class);
    }
    
    public PetShelterDto detailShelter(Long shelterNo) {
    	PetShelterEntity entity = shelterRepository.findByShelterNo(shelterNo)
    	.orElseThrow(() -> new EntityNotFoundException("보호소 정보 없음"));
    	return mapper.map(entity, PetShelterDto.class);
    }
    
    @Transactional
    public PetAbandonmentDto updateAbandonment(PetAbandonmentDto dto) {
        PetAbandonmentEntity entity = abandonmentRepository.findByDesertionNo(dto.getDesertionNo())
            .orElseThrow(() -> new EntityNotFoundException("해당 유기동물이 존재하지 않습니다."));

        entity.setSpecialMark(dto.getSpecialMark());
        entity.setProcessState(dto.getProcessState());
        entity.setIsUpdated(true);
        return mapper.map(entity, PetAbandonmentDto.class); // ModelMapper 등 사용
    }

    @Transactional
    public void updateShelter(PetShelterDto dto) {
        PetShelterEntity existing = shelterRepository.findById(dto.getShelterNo())
        .orElseThrow(() -> new EntityNotFoundException("해당 보호소가 존재하지 않습니다."));
        mapper.map(dto, existing);
    }

    @Transactional
    public void registerShelter(PetShelterDto dto) {
        if (shelterRepository.existsByCareRegNo(dto.getCareRegNo())) {
            throw new IllegalArgumentException("이미 등록된 보호소 코드입니다.");
        }
        PetShelterEntity entity = mapper.map(dto, PetShelterEntity.class);
        entity.setIsUpdated(true); 
        shelterRepository.save(entity);
    }
    
    public PetOriginFacilityDto detailFacility(Long facilityNo) {
        PetFacilityEntity entity = facilityRepository.findByFacilityNo(facilityNo)
            .orElseThrow(() -> new EntityNotFoundException("보호소 정보 없음"));

        log.info("▶ entity: {}", entity); // 🔍 여기에 값 있는지 확인
        PetOriginFacilityDto dto = mapper.map(entity, PetOriginFacilityDto.class);
        log.info("▶ dto: {}", dto); // 🔍 dto에 왜 null 나오는지 체크

        return dto;
    }


}
