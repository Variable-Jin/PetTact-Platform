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

@Service
@RequiredArgsConstructor
public class PetDiaryService {

    private final UserRepository usersRepository;
    private final PetDiaryRepository petDiaryRepository;
    private final RestTemplate restTemplate;
    
    private final String FASTAPI_URL = "http://localhost:8000/generate";

    public String generatePetDiary(String prompt) {
        Map<String, String> body = new HashMap<>();
        body.put("prompt", prompt);

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


    public PetDiaryEntity saveDiary(Users user, Long petId, String diaryContent) {
        Users users = usersRepository.findById(user.getUserNo())
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        PetDiaryEntity entity = PetDiaryEntity.builder()
                //.user(user.getUserNo())
                .petId(petId)
                .diaryContent(diaryContent)
                .build();

        return petDiaryRepository.save(entity);
    }

}
