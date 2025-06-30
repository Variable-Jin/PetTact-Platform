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
import com.pettact.api.user.entity.Users;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor    
@RequestMapping("/diary")
public class PetDiaryController {
	
	private final PetDiaryService diaryService;

	@PostMapping("/create")
	public ResponseEntity<String> createDiary(@RequestBody PetDiaryDto dto, @AuthenticationPrincipal CustomUserDetails user) {
		String generatedDiary = diaryService.generatePetDiary(dto.getPrompt());
	    
	    diaryService.saveDiary(dto.getPetId(), generatedDiary, user.getUserEntity());
	    // Entity → DTO 변환
	    return ResponseEntity.ok("일기가 성공적으로 저장되었습니다.");
	}


}
