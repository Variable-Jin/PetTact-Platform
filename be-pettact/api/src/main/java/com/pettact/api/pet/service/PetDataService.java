package com.pettact.api.pet.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pettact.api.core.base.MapperUtil;
import com.pettact.api.pet.dto.PetAbandonmentDto;
import com.pettact.api.pet.dto.PetKindDto;
import com.pettact.api.pet.dto.PetShelterDto;
import com.pettact.api.pet.dto.PetSidoDto;
import com.pettact.api.pet.dto.PetSigunguDto;
import com.pettact.api.pet.entity.PetAbandonmentEntity;
import com.pettact.api.pet.repository.PetAbandonmentRepository;
import com.pettact.api.pet.repository.PetKindRepository;
import com.pettact.api.pet.repository.PetShelterRepository;
import com.pettact.api.pet.repository.PetSidoRepository;
import com.pettact.api.pet.repository.PetSigunguRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetDataService {

    private final MapperUtil mapper;

    private final PetSidoRepository sidoRepository;
    private final PetSigunguRepository sigunguRepository;
    private final PetKindRepository kindRepository;
    private final PetShelterRepository shelterRepository;
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

    public List<PetShelterDto> getShelterList(String sido, String sigungu) {
        return shelterRepository.findBySidoAndSigungu(sido, sigungu).stream()
            .map(e -> mapper.map(e, PetShelterDto.class))
            .toList();
    }

    public List<PetAbandonmentDto> searchAbandonments(String UpKindCd,String kindCd, String orgNm, String careRegNo) {
    	String kindNm = kindRepository.findKindNmByUpKindCdAndKindCd(UpKindCd,kindCd);
    	System.out.println(kindNm);
        List<PetAbandonmentEntity> results = abandonmentRepository.searchByConditions(kindNm, orgNm, careRegNo);
        return results.stream().map(e -> mapper.map(e, PetAbandonmentDto.class)).toList();
    }

}
