package com.pettact.api.pet.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.pet.dto.PetAbandonmentDto;
import com.pettact.api.pet.service.PetDataService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/pet") 
@RequiredArgsConstructor
public class PetDataController {

    private final PetDataService petDataService;

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
    public ResponseEntity<?> getShelterList(@RequestParam("sido") String sido, @RequestParam("sigungu") String sigungu) {
    	System.out.printf("sido: %s, sigungu: %s\n", sido, sigungu);
        return ResponseEntity.ok(Map.of("items", petDataService.getShelterList(sido, sigungu)));
    }

    @GetMapping("/abandonment")
    public ResponseEntity<?> getAbandonments(
        @RequestParam(value = "upKindCd", required = false) String upKindCd,
        @RequestParam(value = "kindCd", required = false) String kindCd,
        @RequestParam(value = "orgNm", required = false) String orgNm,
        @RequestParam(value = "careRegNo", required = false) String careRegNo
    ) {
        List<PetAbandonmentDto> items = petDataService.searchAbandonments(upKindCd, kindCd, orgNm, careRegNo);
        return ResponseEntity.ok(Map.of("items", items));
    }

}
