package com.pettact.api.pet.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.pet.dto.UserPetDto;
import com.pettact.api.pet.service.UserPetService;
import com.pettact.api.security.vo.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/pet")
@RequiredArgsConstructor
public class UserPetController {

    private final UserPetService userPetService;

    // 반려동물 등록
    @PostMapping
    public ResponseEntity<UserPetDto> save(@AuthenticationPrincipal CustomUserDetails user, @RequestBody UserPetDto dto) {
    	System.out.println(user.getUserEntity().getUserNo());
        dto.setUserNo(user.getUserEntity().getUserNo());
        UserPetDto saved = userPetService.save(dto);
        return ResponseEntity.ok(saved);
    }


    // 반려동물 단건 조회
    @GetMapping("/{petId}")
    public ResponseEntity<UserPetDto> petSearch(@PathVariable("petId") Long petId){
        UserPetDto dto = userPetService.petSearch(petId);
        return ResponseEntity.ok(dto);
    }

    // 반려동물 리스트
    @GetMapping("/list")
    public ResponseEntity<List<UserPetDto>> petListSearch(@AuthenticationPrincipal CustomUserDetails user) {
        List<UserPetDto> pets = userPetService.petListSearch(user.getUserEntity().getUserNo());
        return ResponseEntity.ok(pets);
    }
    //업데이트
    @PutMapping("/{petId}")
    public ResponseEntity<UserPetDto> update(@PathVariable("petId") Long petId, @AuthenticationPrincipal CustomUserDetails user,@RequestBody UserPetDto dto) throws AccessDeniedException {
        dto.setUserNo(user.getUserEntity().getUserNo());
        dto.setPetId(petId); 
        UserPetDto updated = userPetService.update(dto);
        return ResponseEntity.ok(updated);
    }
    
    // 반려동물 삭제 (논리 삭제)
    @DeleteMapping("/{petId}")
    public ResponseEntity<Void> petDelete(@PathVariable("petId") Long petId) {
        userPetService.petDelete(petId);
        return ResponseEntity.ok().build();
    }
    
    
}
