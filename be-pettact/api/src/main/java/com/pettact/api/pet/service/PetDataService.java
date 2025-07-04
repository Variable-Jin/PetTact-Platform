package com.pettact.api.pet.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetDataService {

    private final MapperUtil mapper;

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


}
