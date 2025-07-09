package com.pettact.api.pet.service;

import java.time.Duration;

import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.pettact.api.common.ViewCountService;
import com.pettact.api.common.scheduler.ViewCountScheduler.ViewCountSyncable;
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
public class PetAdminService implements ViewCountSyncable<String> {

    private final PetAbandonmentRepository abandonmentRepository;
    private final PetFacilityRepository facilityRepository;
    private final PetShelterRepository shelterRepository;
    private final ViewCountService viewCountService;
    private final StringRedisTemplate redisTemplate;
    
    private final ModelMapper mapper;

    // ------------------ 유기동물 ------------------

    public PetAbandonmentDto detailAbandonment(String desertionNo, String sessionId) {
        PetAbandonmentEntity entity = abandonmentRepository.findByDesertionNo(desertionNo)
            .orElseThrow(() -> new EntityNotFoundException("유기동물 정보 없음"));
        
        String preventKey = "pet:viewed:" + sessionId + ":" + desertionNo;

        // 중복 조회 방지: key 존재 여부 확인
        if (Boolean.FALSE.equals(redisTemplate.hasKey(preventKey))) {
            // 실제 조회수 INCR key
            viewCountService.increaseViewCount("pet", desertionNo, 60);

            // 중복 방지 key (TTL: 60분 -> 60분동안 중복 조회 방지)
            redisTemplate.opsForValue().set(preventKey, "1", Duration.ofMinutes(60));
        }
        
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

    // ------------------ 유기동물 조회수 ------------------

    public Long increasePetViewCount(String desertionNo) {
        // TTL 5분
        return viewCountService.increaseViewCount("pet", desertionNo, 5);
    }
    
    @Override
    @Transactional
    public void updateViewCount(String desertionNo, int count) {
        abandonmentRepository.updateViewCount(desertionNo, count);
    }
}
