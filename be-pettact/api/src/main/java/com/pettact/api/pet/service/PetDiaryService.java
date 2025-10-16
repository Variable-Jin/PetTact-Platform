package com.pettact.api.pet.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.pettact.api.pet.entity.PetKindEntity;
import com.pettact.api.pet.entity.UserPetEntity;
import com.pettact.api.pet.repository.UserPetRepository;
import org.springframework.beans.factory.annotation.Value;
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
    private final UserPetRepository userPetRepository;

    private final String FASTAPI_CHATBOT_BASE_URL = "https://ooing07-pet-assistance-model.hf.space";
    private final String CHAT_ENDPOINT = "/chat"; // FastAPI app.py의 챗봇 엔드포인트

    @Value("${fastapi.diary.generation.url}")
    private String FASTAPI_DIARY_GENERATION_URL;

    public String getChatbotResponse(String userMessage, Long userNo, Long sessionNo) {

        // ✅ DB에서 사용자의 반려동물 정보 조회
        List<UserPetEntity> userPets = userPetRepository.findByUserUserNo(userNo);

        // ✅ 메시지에서 언급된 반려동물 찾기
        UserPetEntity targetPet = findMentionedPet(userMessage, userPets);

        if (targetPet == null && !userPets.isEmpty()) {
            targetPet = userPets.get(0);
        }

        // ✅ sessionNo 로깅 (히스토리 관리용)
        log.info("채팅 세션: {}, 사용자: {}", sessionNo, userNo);

        // ✅ 허깅페이스 API 호출 (기존 로직 유지)
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_input", userMessage);

        if (targetPet != null) {
            requestBody.put("pet_name", targetPet.getPetName());
            requestBody.put("pet_breed", targetPet.getPetKind() != null ? targetPet.getPetKind().getKindNm() : "알 수 없음");
            requestBody.put("pet_gender", "M".equals(targetPet.getPetGender()) ? "수컷" : "암컷");
            requestBody.put("pet_weight", targetPet.getPetWeight());
            requestBody.put("is_neutered", "Y".equals(targetPet.getIsNeutered()) ? "중성화함" : "중성화 안함");

            String petType = determinePetType(targetPet.getPetKind());
            requestBody.put("pet_type", petType);

            log.info("반려동물 페르소나 활성화: {} ({})", targetPet.getPetName(), petType);
        } else {
            requestBody.put("pet_type", "강아지");
            log.info("반려동물 정보 없음 - 기본 페르소나 사용");
        }

        try {
            String responseJson = restTemplate.postForObject(
                    FASTAPI_CHATBOT_BASE_URL + "/chat",
                    requestBody,
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseJson);

            // ✅ 필요하면 여기서 채팅 히스토리 저장 로직 추가
            // saveChatHistory(sessionNo, userMessage, response);

            return root.get("generated_response").asText();

        } catch (Exception e) {
            log.error("AI 챗봇 API 호출 실패: {}", e.getMessage());
            throw new RuntimeException("AI 챗봇 응답 생성 실패", e);
        }
    }

    // ✅ 메시지에서 언급된 반려동물 찾기
    private UserPetEntity findMentionedPet(String message, List<UserPetEntity> pets) {
        if (message == null || pets == null || pets.isEmpty()) {
            return null;
        }

        // 각 반려동물의 이름이 메시지에 포함되어 있는지 확인
        for (UserPetEntity pet : pets) {
            if (pet.getPetName() != null && message.contains(pet.getPetName())) {
                log.info("메시지에서 반려동물 '{}' 발견", pet.getPetName());
                return pet;
            }
        }

        log.info("메시지에서 반려동물 이름을 찾지 못함");
        return null; // 매칭되는 이름이 없으면 null 반환
    }

    private String determinePetType(PetKindEntity petKind) {
        // API에서 품종명을 정확히 주므로 간단한 문자열 포함 검사만
        return (petKind != null && petKind.getKindNm() != null &&
                petKind.getKindNm().contains("고양이")) ? "고양이" : "강아지";
    }

    public Map<String, Object> generatePetDiary(String prompt, Long petId) {
        try {
            // ✅ 1. 입력 파라미터 로깅
            log.info("=== 일기 생성 시작 ===");
            log.info("입력 prompt: {}", prompt);
            log.info("입력 petId: {}", petId);

            UserPetEntity pet = null;
            if (petId != null) {
                pet = userPetRepository.findById(petId).orElse(null);
                // ✅ 2. 조회된 pet 정보 로깅
                if (pet != null) {
                    log.info("조회된 pet 정보:");
                    log.info("  - pet.getPetName(): {}", pet.getPetName());
                    log.info("  - pet.getPetKind(): {}", pet.getPetKind());
                    if (pet.getPetKind() != null) {
                        log.info("  - pet.getPetKind().getKindNm(): {}", pet.getPetKind().getKindNm());
                    }

                    // ownerRelation 확인
                    try {
                        log.info("  - pet.getOwnerRelation(): {}", pet.getOwnerRelation());
                    } catch (Exception e) {
                        log.info("  - ownerRelation 필드 없음: {}", e.getMessage());
                    }

                    // determinePetType 결과 확인
                    String petType = determinePetType(pet.getPetKind());
                    log.info("  - determinePetType 결과: {}", petType);
                } else {
                    log.warn("petId {}에 해당하는 pet을 찾을 수 없음", petId);
                }
            }

            Map<String, Object> body = new HashMap<>();
            body.put("topic", prompt);

            if (pet != null) {
                String petName = pet.getPetName();
                body.put("pet_name", petName);
                log.info("FastAPI 요청에 설정된 pet_name: {}", petName);

                // ✅ ownerRelation 필드가 없을 수 있으니 체크
                String ownerRelation = "주인님"; // 기본값
                try {
                    if (pet.getOwnerRelation() != null) {
                        ownerRelation = pet.getOwnerRelation();
                        log.info("pet에서 가져온 ownerRelation: {}", ownerRelation);
                    }
                } catch (Exception e) {
                    log.warn("ownerRelation 필드 접근 실패, 기본값 사용: {}", e.getMessage());
                }
                body.put("owner_relation", ownerRelation);
                log.info("FastAPI 요청에 설정된 owner_relation: {}", ownerRelation);

                String petType = determinePetType(pet.getPetKind());
                body.put("pet_type", petType);
                log.info("FastAPI 요청에 설정된 pet_type: {}", petType);

            } else {
                // 기본값들
                body.put("pet_name", "반려동물");
                body.put("owner_relation", "주인님");
                body.put("pet_type", "강아지");
                log.info("pet이 null이므로 기본값 사용");
            }

            // ✅ 3. 최종 요청 데이터 로깅
            log.info("FastAPI로 보낼 최종 요청 데이터: {}", body);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    FASTAPI_DIARY_GENERATION_URL + "/diary/generate",
                    body,
                    String.class
            );

            // ✅ 4. 응답 상태 코드 및 원본 응답 로깅
            log.info("FastAPI 응답 상태 코드: {}", response.getStatusCode());
            log.info("FastAPI 원본 응답: {}", response.getBody());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            // ✅ 5. JSON 파싱 결과 로깅
            log.info("파싱된 JSON root: {}", root.toString());

            // ✅ 일기 내용과 이미지 URL 모두 추출
            JsonNode diaryNode = root.get("diaryContent");
            JsonNode imageNode = root.get("imageUrl");

            log.info("diaryContent 노드: {}", diaryNode);
            log.info("imageUrl 노드: {}", imageNode);

            Map<String, Object> result = new HashMap<>();

            // 일기 내용 처리
            if (diaryNode != null && !diaryNode.isNull()) {
                String diaryContent = diaryNode.asText();
                result.put("diaryContent", diaryContent);
                log.info("성공적으로 추출된 일기 내용: {}", diaryContent);
            } else {
                // 대안 키 확인
                JsonNode generatedDiaryNode = root.get("generated_diary");
                log.info("generated_diary 노드: {}", generatedDiaryNode);

                if (generatedDiaryNode != null && !generatedDiaryNode.isNull()) {
                    String diaryContent = generatedDiaryNode.asText();
                    result.put("diaryContent", diaryContent);
                    log.info("대안 키에서 추출된 일기 내용: {}", diaryContent);
                } else {
                    log.error("일기 내용을 찾을 수 없음");
                    result.put("diaryContent", "오늘도 행복한 하루였다멍! 🐕");
                }
            }

            // 이미지 URL 처리
            if (imageNode != null && !imageNode.isNull()) {
                String imageUrl = imageNode.asText();
                result.put("imageUrl", imageUrl);
                log.info("성공적으로 추출된 이미지 URL: {}", imageUrl);
            } else {
                result.put("imageUrl", null);
                log.info("이미지 URL이 null이거나 존재하지 않음");
            }

            result.put("status", "success");

            log.info("=== 최종 반환 데이터 ===");
            log.info("diaryContent: {}", result.get("diaryContent"));
            log.info("imageUrl: {}", result.get("imageUrl"));
            log.info("=== 일기 생성 성공 ===");

            return result;

        } catch (Exception e) {
            log.error("=== 일기 생성 실패 ===");
            log.error("에러 메시지: {}", e.getMessage());
            log.error("에러 스택 트레이스: ", e);

            // 실패 시에도 Map 반환
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("diaryContent", "오늘도 행복한 하루였다멍! 🐕");
            errorResult.put("imageUrl", null);
            errorResult.put("status", "error");
            return errorResult;
        }
    }


    // 일기 저장 기능 (CRUD - Create)
    public PetDiaryEntity saveDiary(Long petId, String diaryContent, Users user, String prompt) {
        PetDiaryEntity entity = PetDiaryEntity.builder()
                .user(user)
                .petId(petId)
                .diaryContent(diaryContent)
                .prompt(prompt)
                .build();

        return petDiaryRepository.save(entity);
    }

    // 일기 삭제 기능 (CRUD - Delete)
    @Transactional
    public void deleteDiary(Long diaryId, Long userId) throws AccessDeniedException { // AccessDeniedException 추가
        PetDiaryEntity diary = petDiaryRepository.findById(diaryId)
                .orElseThrow(() -> new EntityNotFoundException("일기가 존재하지 않습니다."));

        if (!diary.getUser().getUserNo().equals(userId)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }
        diary.softDelete(); // softDelete() 메서드가 PetDiaryEntity에 정의되어 있어야 합니다.
        petDiaryRepository.save(diary);
    }

    // 일기 목록 조회 기능 (CRUD - Read)
    public List<PetDiaryDto> diaryList(Long petId) {
        List<PetDiaryEntity> list = petDiaryRepository.findByPetIdAndIsDeletedFalse(petId);
        return list.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // DTO 변환 메서드 (유지)
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

