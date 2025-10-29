package com.pettact.api.pet.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.common.dto.PageResponseDto;
import com.pettact.api.pet.dto.PetAbandonmentDto;
import com.pettact.api.pet.dto.PetFacilityDto;
import com.pettact.api.pet.dto.PetOriginFacilityDto;
import com.pettact.api.pet.dto.PetShelterDto;
import com.pettact.api.pet.entity.PetSidoEntity;
import com.pettact.api.pet.entity.PetSigunguEntity;
import com.pettact.api.pet.repository.PetSidoRepository;
import com.pettact.api.pet.repository.PetSigunguRepository;
import com.pettact.api.pet.service.PetDataService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/pet") 
@RequiredArgsConstructor
public class PetDataController {

    private final PetDataService petDataService;
    
    private final PetSidoRepository sidoRepository;
    
    private final PetSigunguRepository sigunguRepository;
    @GetMapping("/sido")
    public ResponseEntity<?> getSidoList() {
        return ResponseEntity.ok(Map.of("items", petDataService.getSidoList()));
    }

    @GetMapping("/sigungu")
    public ResponseEntity<?> getSigunguList(@RequestParam("uprCd") String uprCd) {
        return ResponseEntity.ok(Map.of("items", petDataService.getSigunguList(uprCd)));
    }

    @GetMapping("/kind")
    public ResponseEntity<?> getKindList(@RequestParam("upKindCd") String upKindCd) {
        return ResponseEntity.ok(Map.of("items", petDataService.getKindList(upKindCd)));
    }

    @GetMapping("/shelter")
    public ResponseEntity<?> getShelterList(
        @RequestParam(value = "sido", required = false) String sido,
        @RequestParam(name = "page", defaultValue = "1") int page,
        @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Page<PetShelterDto> result = petDataService.getShelterList(sido, page, size);
        return ResponseEntity.ok(Map.of(
            "content", result.getContent(),
            "totalPages", result.getTotalPages(),
            "totalElements", result.getTotalElements()
        ));
        
    }
    
    @GetMapping("/facility")
    public ResponseEntity<?> getFacilityList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sidoCode", required = false) String sidoCode,
            @RequestParam(value = "sigunguCode", required = false) String sigunguCode,
            @RequestParam(value = "facilityName", required = false) String facilityName
    ) { String sidoName = null;
    	String sigunguName = null;

    	if (sidoCode != null && !sidoCode.isEmpty()) {
    	    PetSidoEntity sido = sidoRepository.findByOrgCd(sidoCode);
    	    if (sido != null) sidoName = sido.getOrgdownNm();
    	}

    	if (sigunguCode != null && !sigunguCode.isEmpty()) {
    	    PetSigunguEntity sigungu = sigunguRepository.findByOrgCd(sigunguCode);
    	    if (sigungu != null) sigunguName = sigungu.getOrgdownNm();
    	}

    	Pageable pageable = PageRequest.of(page - 1, size);
    	Page<PetOriginFacilityDto> result = petDataService.getFacilityList(sidoName, sigunguName, facilityName, pageable);
        return ResponseEntity.ok(Map.of(
                "content", result.getContent(),
                "totalPages", result.getTotalPages(),
                "totalElements", result.getTotalElements()
            ));
    }


    
    @GetMapping("/abandonment")
    public ResponseEntity<?> getAbandonmentList(
        @RequestParam(value = "upKindCd", required = false) String upKindCd,
        @RequestParam(value = "kindCd", required = false) String kindCd,
        @RequestParam(value = "orgNm", required = false) String orgNm,
        @RequestParam(value = "careRegNo", required = false) String careRegNo,
        @RequestParam(name = "page", defaultValue = "1") int page,
        @RequestParam(name = "size", defaultValue = "10") int size 
    ) {
        Page<PetAbandonmentDto> result = petDataService.getAbandonmentList(upKindCd, kindCd, orgNm, page, size);
        return ResponseEntity.ok(Map.of(
            "content", result.getContent(),
            "totalPages", result.getTotalPages(),
            "totalElements", result.getTotalElements()
        ));
    }


    /**
     * Hugging Space api 데이터 json export -> rag 적용
     */
    @GetMapping("/export-facilities")
    public ResponseEntity<List<PetOriginFacilityDto>> exportFacilities() {
        Pageable pageable = PageRequest.of(0, 25000); // 충분히 큰 사이즈
        Page<PetOriginFacilityDto> result = petDataService.getFacilityList(null, null, null, pageable);
        return ResponseEntity.ok(result.getContent());
    }

    @GetMapping("/export-shelters")
    public ResponseEntity<List<PetShelterDto>> exportShelters() {
        Page<PetShelterDto> result = petDataService.getShelterList(null, 1, 500);
        return ResponseEntity.ok(result.getContent());
    }

    @GetMapping("/export-abandonment")
    public ResponseEntity<List<PetAbandonmentDto>> exportAbandonment() {
        Page<PetAbandonmentDto> result = petDataService.getAbandonmentList(null, null, null, 1, 8000);
        return ResponseEntity.ok(result.getContent());
    }

    @GetMapping("/abandonment/ending-soon")
    public PageResponseDto<PetAbandonmentDto> getEndingSoonAbandonments(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return petDataService.getEndingSoonAbandonments(page, size);
    }
    
    // ------------------ main page 노출용  ------------------
    @GetMapping("/abandonment/ending-soon/main")
    public List<PetAbandonmentDto> getEndingSoonAbandonmentsForMain(
    	    @RequestParam(value = "count", defaultValue = "5") int count
    ) {
	    return petDataService.getEndingSoonAbandonmentsForMain(count);
	}
}
