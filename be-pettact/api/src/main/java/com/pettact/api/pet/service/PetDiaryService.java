package com.pettact.api.pet.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pettact.api.pet.entity.PetDiaryEntity;
import com.pettact.api.pet.repository.PetDiaryRepository;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetDiaryService {

    private final UserRepository usersRepository;
    private final PetDiaryRepository petDiaryRepository;
    private final RestTemplate restTemplate;
    
    private final String FASTAPI_URL = "http://192.168.4.149:8000/generate";

    public String generatePetDiary(String prompt) {
        Map<String, String> body = new HashMap<>();
        body.put("prompt", prompt);
        log.info("{}", prompt);
        ResponseEntity<String> response = restTemplate.postForEntity(
        	FASTAPI_URL,  // ex: http://localhost:8000/generate
            body,
            String.class
        );

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.get("generated_diary").asText();  // 이 줄이 핵심
        } catch (Exception e) {
            throw new RuntimeException("AI 일기 응답 파싱 실패", e);
        }
    }

    
    
    public PetDiaryEntity saveDiary(Long petId, String diaryContent) {

        PetDiaryEntity entity = PetDiaryEntity.builder()
                .petId(1L)
                .diaryContent(diaryContent)
                .build();

        return petDiaryRepository.save(entity);
    }

}
