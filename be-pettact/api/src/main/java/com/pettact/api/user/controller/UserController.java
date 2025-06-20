package com.pettact.api.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.user.dto.UserJoinDTO;
import com.pettact.api.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid UserJoinDTO dto) {
    	try {
    		userService.join(dto);
    		return ResponseEntity.ok().build();
		} catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest().body(e.getMessage()); // Service
	    } catch (Exception e) {
			return ResponseEntity.status(500).body("회원가입 중 오류가 발생했습니다. 다시 시도해주세요.");
		}
    }
    
    
}
