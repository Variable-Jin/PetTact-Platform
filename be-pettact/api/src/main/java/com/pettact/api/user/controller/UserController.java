package com.pettact.api.user.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.security.service.EmailService;
import com.pettact.api.security.util.VerificationCodeStore;
import com.pettact.api.user.dto.UserJoinDTO;
import com.pettact.api.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    private final VerificationCodeStore verificationCodeStore;

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
    
    // 이메일 인증 요청(링크)
    @PostMapping("/email/send")
    public ResponseEntity<?> sendVerificationEmail(@RequestParam("userEmail") String userEmail){
    	String token = UUID.randomUUID().toString();
        verificationCodeStore.saveCode("email-token:" + token, userEmail);
        emailService.sendVerificationLink(userEmail, token);
        return ResponseEntity.ok("이메일 인증 링크를 전송했습니다.");
    }
    
    // 이메일 인증 링크 클릭
    @GetMapping("/email/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        String email = verificationCodeStore.getCode("email-token:" + token);

        if (email == null) {
            return ResponseEntity.badRequest().body("유효하지 않거나 만료된 인증 링크입니다.");
        }

        verificationCodeStore.saveCode("verified:" + email, "true");
        verificationCodeStore.remove("email-token:" + token);
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }
}
