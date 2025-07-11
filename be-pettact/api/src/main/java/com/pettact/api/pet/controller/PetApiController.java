package com.pettact.api.pet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.pet.dto.PetAbandonmentDto;
import com.pettact.api.pet.dto.PetOriginFacilityDto;
import com.pettact.api.pet.dto.PetShelterDto;
import com.pettact.api.pet.service.PetAdminService;
import com.pettact.api.pet.service.PetDataInitService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api")
public class PetApiController {

    private final PetDataInitService petDataInitService;
    private final PetAdminService petAdminService;

    // ------------------ api 데이터 최초 db 축적  ------------------

    @GetMapping("/fetch")
    public String dataInit() {
        petDataInitService.fetchAllApi();
        return "반려동물 편의시설 데이터 수집 완료!";
    }

    // ------------------ 유기동물 (abandonment) ------------------

    @GetMapping("/abandonment/{desertionNo}")
    public ResponseEntity<PetAbandonmentDto> detailAbandonment(@PathVariable("desertionNo") String desertionNo,  HttpSession session) {
    	String sessionId = session.getId();
        return ResponseEntity.ok(petAdminService.detailAbandonment(desertionNo, sessionId));
    }

    @PutMapping("/abandonment/{desertionNo}")
    public ResponseEntity<PetAbandonmentDto> updateAbandonment(
        @PathVariable("desertionNo") String desertionNo,
        @RequestBody PetAbandonmentDto dto) {

        dto.setDesertionNo(desertionNo);
        PetAbandonmentDto updated = petAdminService.updateAbandonment(dto);
        return ResponseEntity.ok(updated);
    }

    // ------------------ 편의시설 (facility) ------------------
    @GetMapping("/facility/{facilityNo}")
    public ResponseEntity<?> getFacility(@PathVariable("facilityNo") Long facilityNo) {
        return ResponseEntity.ok(petAdminService.detailFacility(facilityNo));
    }
//
//    @PostMapping("/facility")
//    public ResponseEntity<String> createFacility(@RequestBody PetFacilityDto dto) {
//        petFacilityService.create(dto);
//        return ResponseEntity.ok("편의시설 등록 완료");
//    }
//
//    @PutMapping("/facility/{id}")
//    public ResponseEntity<String> updateFacility(@PathVariable Long id, @RequestBody PetFacilityDto dto) {
//        petFacilityService.update(id, dto);
//        return ResponseEntity.ok("편의시설 수정 완료");
//    }
//
//    @PutMapping("/facility/{id}")
//    public ResponseEntity<String> deleteFacility(@PathVariable Long id) {
//        petFacilityService.delete(id);
//        return ResponseEntity.ok("편의시설 삭제 완료");
//    }
//
//    // ------------------ 보호소 (shelter) ------------------

    @GetMapping("/shelter/{shelterNo}")
    public ResponseEntity<?> getShelterDetail(@PathVariable("shelterNo") Long shelterNo) {
        return ResponseEntity.ok(petAdminService.detailShelter(shelterNo));
    }
    

    @PutMapping("/shelter/{shelterNo}")
    public ResponseEntity<?> updateShelter(@PathVariable("shelterNo") Long shelterNo, @RequestBody PetShelterDto dto) {
        dto.setShelterNo(shelterNo); 
        petAdminService.updateShelter(dto);
        return ResponseEntity.ok("보호소 수정 완료");
    }
    
    @PostMapping("/shelter")
    public ResponseEntity<?> RegisterShelter(@RequestBody PetShelterDto dto) {
        petAdminService.registerShelter(dto);
        return ResponseEntity.ok("보호소 등록 완료");
    }
}
