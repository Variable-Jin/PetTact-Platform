package com.pettact.api.pet.controller;

import com.pettact.api.pet.service.PetDataInitService;
import com.pettact.api.pet.service.PetDataMergeService;
import com.pettact.api.pet.service.PetTmpDataService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PetApiController {

    private final PetDataInitService petDataInitService;
    private final PetDataMergeService petDataMergeService;
    private final PetTmpDataService petTmpDataService;
    // fetch 호출 시 시설 데이터 수집 
    @GetMapping("/fetch")
    public String dataInit() {
    	petDataInitService.fetchAllApi(); // 첫 데이터 init 
    	//petTmpDataService.fetchAllTmpApi(); // merge용 tmp 데이터 
    	// petDataMergeService.mergeAll(); // 머지실행
    	return "반려동물 편의시설 데이터 수집 완료!";
    }
}
