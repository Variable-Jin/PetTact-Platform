package com.pettact.api.pet.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pettact.api.pet.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.pettact.api.pet.entity.PetDiaryEntity;
import com.pettact.api.pet.service.PetDiaryService;
import com.pettact.api.security.vo.CustomUserDetails;
import com.pettact.api.user.entity.Users;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor    
@RequestMapping("/v1/diary")
@Slf4j
public class PetDiaryController {

	private final PetDiaryService diaryService;

//	@PostMapping("/create")
//	public ResponseEntity<Map<String, Object>> createDiary(@RequestBody PetDiaryDto dto, @AuthenticationPrincipal CustomUserDetails user) {
//		// 1단계: FastAPI로 일기 + 이미지 생성 (Map으로 받음)
//		Map<String, Object> generatedContent = diaryService.generatePetDiary(dto.getPrompt(), dto.getPetId());
//
//		String diaryContent = (String) generatedContent.get("diaryContent");
//		String imageUrl = (String) generatedContent.get("imageUrl");
//
//		// 2단계: 생성된 일기를 DB에 저장
//		diaryService.saveDiary(dto.getPetId(), diaryContent, user.getUserEntity(), dto.getPrompt());
//
//		// ✅ 생성된 일기 내용과 이미지 URL 모두 반환
//		Map<String, Object> response = new HashMap<>();
//		response.put("status", "saved");
//		response.put("diaryContent", diaryContent);
//		response.put("imageUrl", imageUrl);
//
//		return ResponseEntity.ok(response);
//	}

	@GetMapping("/{petId}")
	public ResponseEntity<List<PetDiaryDto>> diaryList(@PathVariable("petId") Long petId) {
		return ResponseEntity.ok(diaryService.diaryList(petId));
	}

	@PutMapping("/{diaryId}")
	public ResponseEntity<String> deleteDiary(@PathVariable("diaryId") Long diaryId, @AuthenticationPrincipal CustomUserDetails user) {
		diaryService.deleteDiary(diaryId, user.getUserEntity().getUserNo());
		return ResponseEntity.ok("일기가 삭제되었습니다.");
	}

	/**
	 * 챗봇 메시지 전송 엔드포인트 (새로 추가)
	 * Vue.js 프론트엔드로부터 채팅 메시지를 받아 챗봇 응답을 생성합니다.
	 * @param requestDto Vue.js에서 전송된 요청 데이터 (메시지, 사용자 ID, 세션 ID, 반려동물 종류, 반려동물 ID)
	 * @return 챗봇 응답을 포함하는 JSON 객체
	 */
	@PostMapping("/chat/send")
	public ResponseEntity<Map<String, String>> sendChatMessage(@RequestBody assistanceDto requestDto,
															   @AuthenticationPrincipal CustomUserDetails user) {

		try {
			// ✅ 로그 추가
			log.info("채팅 요청 수신: {}", requestDto);
			log.info("인증된 사용자: {}", user != null ? user.getUserEntity().getUserNo() : "null");

			// ✅ 개별 파라미터로 전달
			String chatbotResponse = diaryService.getChatbotResponse(
					requestDto.getMessage(),
					requestDto.getUserNo(),
					requestDto.getSessionNo()
			);

			log.info("챗봇 응답 생성 완료: {}", chatbotResponse);

			return ResponseEntity.ok(Map.of("response", chatbotResponse));

		} catch (Exception e) {
			log.error("채팅 처리 실패: {}", e.getMessage());
			return ResponseEntity.status(500)
					.body(Map.of("response", "죄송합니다. 일시적인 오류가 발생했습니다."));
		}
	}

	// 히스토리 엔드포인트도 추가
	@GetMapping("/chat/history")  // /v1/chat/history
	public ResponseEntity<?> getChatHistory(
			@RequestParam String sessionId,
			@RequestParam String userId) {
		return ResponseEntity.ok(Map.of("messages", new java.util.ArrayList<>()));
	}

	@PostMapping("/chat/save")  // /v1/chat/save
	public ResponseEntity<?> saveChatHistory(@RequestBody Map<String, Object> request) {
		return ResponseEntity.ok(Map.of("status", "success"));
	}



}