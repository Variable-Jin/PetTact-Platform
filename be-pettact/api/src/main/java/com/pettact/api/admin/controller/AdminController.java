package com.pettact.api.admin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.admin.dto.AdminUserDTO;
import com.pettact.api.admin.service.AdminService;
import com.pettact.api.code.service.CommonCodeService;
import com.pettact.api.user.entity.Users;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
public class AdminController {
	private final AdminService adminService;
	private final CommonCodeService commonCodeService;
	
	// 관리자 responseEntity 응답 형식 -> 에러 출력 e.getMessage() 로 처리
	
	// 회원 목록
	@GetMapping("/userList")
	public ResponseEntity<?> getUserList(
	        @RequestParam(value = "keyword", required = false) String keyword,
	        @RequestParam(value = "status", required = false) String status,
	        @RequestParam(value = "role", required = false) String role,
	        @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
	        @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
	    
	    try {
	        // LocalDate -> LocalDateTime 변환
	        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
	        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;
	        
	        List<AdminUserDTO> userList = adminService.getUserList(keyword, status, role, startDateTime, endDateTime);
	            
	        return ResponseEntity.ok().body(userList);
	    } catch (Exception e) {
	        return ResponseEntity.badRequest()
	            .body("회원 목록을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
	    }
	}
	
	// 필터 옵션들
	@GetMapping("/userList/filters")
	public ResponseEntity<?> getFilterOptions() {
	    try {
	        Map<String, Object> filterOptions = new HashMap<>();

	        filterOptions.put("statusOptions", commonCodeService.getCodeOptions("USER_STATUS"));
	        filterOptions.put("roleOptions", commonCodeService.getCodeOptions("USER_ROLE"));

	        return ResponseEntity.ok().body(filterOptions);
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().
	        		body("필터 옵션 조회 실패" + e.getMessage());
	    }
	}
	
	// 회원 잠금
	@PostMapping("/lockUser/{userNo}")
	public ResponseEntity<?> lockUser(@PathVariable("userNo") Long userNo){
	    try {
	        boolean result = adminService.lockUserByUserNo(userNo);

	        if(result) {
	            return ResponseEntity.ok().body("해당 회원이 잠금 처리되었습니다.");
	        } else {
	            return ResponseEntity.badRequest().body("이미 잠금 상태이거나 처리할 수 없습니다.");
	        }
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body("회원 잠금 기능 중 오류가 발생했습니다: " + e.getMessage());
	    }
	}
	
	// 회원 잠금 해제
	@PostMapping("/unlockUser/{userNo}")
	public ResponseEntity<?> unlockUser(@PathVariable("userNo") Long userNo){
	    try {
	        boolean result = adminService.unlockUserByUserNo(userNo);

	        if(result) {
	            return ResponseEntity.ok().body("해당 회원이 잠금 해제 처리되었습니다.");
	        } else {
	            return ResponseEntity.badRequest().body("이미 잠금 해제 상태이거나 처리할 수 없습니다.");
	        }
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body("회원 잠금 해제 기능 중 오류가 발생했습니다: " + e.getMessage());
	    }
	}
	
	// 판매자 권한 승인
	
	
	// 판매자 활동 내역 조회
	
	
	// 
}
