package com.pettact.api.pet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.pet.dto.PetDiaryDto;
import com.pettact.api.pet.entity.PetDiaryEntity;
import com.pettact.api.pet.service.PetDiaryService;
import com.pettact.api.security.vo.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor    
@RequestMapping("/diary")
public class PetDiaryController {
	
	private final PetDiaryService diaryService;
	
	@PostMapping("/create")
	public ResponseEntity<PetDiaryEntity> createDiary(@RequestBody PetDiaryDto dto, @AuthenticationPrincipal CustomUserDetails user) {
	    // 프론트에서 보낸 prompt 그대로 전달
	    String generatedDiary = diaryService.generatePetDiary(dto.getPrompt());

	    // 생성된 일기 저장
	    PetDiaryEntity saved = diaryService.saveDiary(user.getUserEntity(), dto.getPetId(), generatedDiary);

	    return ResponseEntity.ok(saved);
	}

}
