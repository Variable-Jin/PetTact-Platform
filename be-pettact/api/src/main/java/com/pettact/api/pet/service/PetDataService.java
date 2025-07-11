package com.pettact.api.pet.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pettact.api.common.dto.PageResponseDto;
import com.pettact.api.common.util.PageUtil;
import com.pettact.api.core.base.MapperUtil;
import com.pettact.api.pet.dto.PetAbandonmentDto;
import com.pettact.api.pet.dto.PetFacilityDto;
import com.pettact.api.pet.dto.PetKindDto;
import com.pettact.api.pet.dto.PetOriginFacilityDto;
import com.pettact.api.pet.dto.PetShelterDto;
import com.pettact.api.pet.dto.PetSidoDto;
import com.pettact.api.pet.dto.PetSigunguDto;
import com.pettact.api.pet.entity.PetAbandonmentEntity;
import com.pettact.api.pet.entity.PetFacilityEntity;
import com.pettact.api.pet.entity.PetShelterEntity;
import com.pettact.api.pet.repository.PetAbandonmentRepository;
import com.pettact.api.pet.repository.PetFacilityRepository;
import com.pettact.api.pet.repository.PetKindRepository;
import com.pettact.api.pet.repository.PetShelterRepository;
import com.pettact.api.pet.repository.PetSidoRepository;
import com.pettact.api.pet.repository.PetSigunguRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetDataService {

    private final MapperUtil mapper;
    private final ObjectMapper objectMapper;
//    private final RedisTemplate<String, List<PetAbandonmentDto>> petAbandonmentDtoListRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    private final PetSidoRepository sidoRepository;
    private final PetSigunguRepository sigunguRepository;
    private final PetKindRepository kindRepository;
    private final PetShelterRepository shelterRepository;
    private final PetFacilityRepository facilityRepository;
    private final PetAbandonmentRepository abandonmentRepository;

    public List<PetSidoDto> getSidoList() {
        return sidoRepository.findAll().stream()
            .map(e -> mapper.map(e, PetSidoDto.class))
            .toList();
    }

    public List<PetSigunguDto> getSigunguList(String uprCd) {
        return sigunguRepository.findByUprCd(uprCd).stream()
            .map(e -> mapper.map(e, PetSigunguDto.class))
            .toList();
    }

    public List<PetKindDto> getKindList(String upKindCd) {
        return kindRepository.findByUpKindCd(upKindCd).stream()
            .map(e -> mapper.map(e, PetKindDto.class))
            .toList();
    }

    public Page<PetShelterDto> getShelterList(String sido, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("careNm").ascending());
        Page<PetShelterEntity> result = shelterRepository.findBySido(sido,pageable);
        return result.map(entity -> mapper.map(entity, PetShelterDto.class));
    }
    
    
    public Page<PetAbandonmentDto> getAbandonmentList(
    	    String upKindCd, String kindCd, String orgNm, int page, int size) {
    	    String kindNm = kindRepository.findKindNmByUpKindCdAndKindCd(upKindCd, kindCd);
    	    Pageable pageable = PageRequest.of(page - 1, size, Sort.by("noticeSdt").descending());
    	    Page<PetAbandonmentEntity> result = abandonmentRepository.searchByConditionsPaged(kindNm, orgNm, pageable);
    	    return result.map(entity -> mapper.map(entity, PetAbandonmentDto.class));
    	}

    public Page<PetOriginFacilityDto> getFacilityList(String sidoName, String sigunguName, String facilityName, Pageable pageable) {
        Page<PetFacilityEntity> facilities = facilityRepository.searchByConditions(sidoName, sigunguName, facilityName, pageable);
        
        return facilities.map(f -> mapper.map(f, PetOriginFacilityDto.class));
    }

    // ------------------ 공고 종료 임박 데이터 캐싱  ------------------
    private List<PetAbandonmentDto> getEndingSoonListFromCache() {
        String json = (String) redisTemplate.opsForValue().get("pet:endingSoon:list");

        try {
            if (json != null) {
                return objectMapper.readValue(json, new TypeReference<List<PetAbandonmentDto>>() {});
            }
        } catch (Exception e) {
            log.error("JSON 역직렬화 오류", e);
        }

        return List.of();
    }
    
    public PageResponseDto<PetAbandonmentDto> getEndingSoonAbandonments(int page, int size) {
        List<PetAbandonmentDto> all = getEndingSoonListFromCache();
        int total = all.size();

        List<PetAbandonmentDto> pagedList = PageUtil.getPagedList(all, page, size);
        return new PageResponseDto<>(pagedList, total, page, size);
    }
    
    public List<PetAbandonmentDto> getEndingSoonAbandonmentsForMain(int count) {
        List<PetAbandonmentDto> all = getEndingSoonListFromCache();
        
        if (all.isEmpty()) return List.of();
        all.sort(Comparator.comparingLong(PetAbandonmentDto::getPetViewCnt));

        int excludeCount = (int) Math.ceil(all.size() * 0.2);
        List<PetAbandonmentDto> filtered = all.subList(excludeCount, all.size());

        Collections.shuffle(filtered);

        return filtered.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

}
