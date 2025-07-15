package com.pettact.api.pet.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
@RequestMapping("/v1/diary")
public class PetDiaryController {
	
	private final PetDiaryService diaryService;

	@PostMapping("/create")
	public ResponseEntity<String> createDiary(@RequestBody PetDiaryDto dto,@AuthenticationPrincipal CustomUserDetails user){
		String generatedDiary = diaryService.generatePetDiary(dto.getPrompt(), dto.getPetId());
	    diaryService.saveDiary(dto.getPetId(), generatedDiary, user.getUserEntity(), dto.getPrompt());

	    return ResponseEntity.ok("saved"); 
	}

	@GetMapping("/{petId}")
	public ResponseEntity<List<PetDiaryDto>> diaryList(@PathVariable("petId") Long petId) {
	    return ResponseEntity.ok(diaryService.diaryList(petId));
	}

	@PutMapping("/{diaryId}")
	public ResponseEntity<String> deleteDiary(@PathVariable("diaryId") Long diaryId, @AuthenticationPrincipal CustomUserDetails user) {
	    diaryService.deleteDiary(diaryId, user.getUserEntity().getUserNo());
	    return ResponseEntity.ok("일기가 삭제되었습니다.");
	}


}
