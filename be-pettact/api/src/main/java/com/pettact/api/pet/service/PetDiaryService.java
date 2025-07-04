package com.pettact.api.pet.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pettact.api.core.base.MapperUtil;
import com.pettact.api.pet.dto.PetDiaryDto;
import com.pettact.api.pet.entity.PetDiaryEntity;
import com.pettact.api.pet.repository.PetDiaryRepository;
import com.pettact.api.user.entity.Users;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetDiaryService {

    private final PetDiaryRepository petDiaryRepository;
    private final RestTemplate restTemplate;
    private final MapperUtil mapperUtil;
    private final String FASTAPI_URL = "http://192.168.4.149:8000/generate"; // ip주소 변경시 같이 변경할것 

    public String generatePetDiary(String prompt, Long petId) { // petId혹시 필요하면 prompt와 같이 전송하기 위해 넣어놓았습니다.
        Map<String, String> body = new HashMap<>();
        body.put("prompt", prompt);
        log.info("{}", prompt);
        ResponseEntity<String> response = restTemplate.postForEntity(
        	FASTAPI_URL, 
            body,
            String.class
        );

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.get("generated_diary").asText(); 
        } catch (Exception e) {
            throw new RuntimeException("AI 일기 응답 파싱 실패", e);
        }
    }

    
    
    public PetDiaryEntity saveDiary(Long petId, String diaryContent, Users user, String prompt) {
        PetDiaryEntity entity = PetDiaryEntity.builder()
                .user(user)
                .petId(petId)
                .diaryContent(diaryContent)
                .prompt(prompt)
                .build();

        return petDiaryRepository.save(entity); 
    }

    @Transactional
    public void deleteDiary(Long diaryId, Long userId) {
        PetDiaryEntity diary = petDiaryRepository.findById(diaryId)
            .orElseThrow(() -> new EntityNotFoundException("일기가 존재하지 않습니다."));

        if (!diary.getUser().getUserNo().equals(userId)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }
        diary.softDelete();
        petDiaryRepository.save(diary);  
    }

    public List<PetDiaryDto> diaryList(Long petId) {
        List<PetDiaryEntity> list = petDiaryRepository.findByPetIdAndIsDeletedFalse(petId);
        return list.stream()
                   .map(this::toDto)
                   .collect(Collectors.toList());
    }

    
    
    private PetDiaryDto toDto(PetDiaryEntity entity) {
        PetDiaryDto dto = new PetDiaryDto();
        dto.setPetId(entity.getPetId());
        dto.setDiaryId(entity.getDiaryId());
        dto.setPrompt(entity.getPrompt()); 
        dto.setDiaryContent(entity.getDiaryContent());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }


}
