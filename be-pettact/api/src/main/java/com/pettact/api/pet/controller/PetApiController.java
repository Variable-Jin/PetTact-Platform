package com.pettact.api.pet.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.pet.service.PetDataInitService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PetApiController {

    private final PetDataInitService petDataInitService;
    // fetch 호출 시 시설 데이터 수집 
    @GetMapping("/fetch")
    public String dataInit() {
    	petDataInitService.fetchAllApi(); 
    	return "반려동물 편의시설 데이터 수집 완료!";
    }
}
