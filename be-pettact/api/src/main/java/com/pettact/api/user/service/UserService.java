package com.pettact.api.user.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pettact.api.code.entity.CommonCode;
import com.pettact.api.code.repository.CommonCodeRepository;
import com.pettact.api.security.util.VerificationCodeStore;
import com.pettact.api.user.dto.UserJoinDTO;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final CommonCodeRepository commonCodeRepository;
	private final PasswordEncoder passwordEncoder;
	private final VerificationCodeStore verificationCodeStore;
	
    public void join(UserJoinDTO dto) {
        
    	if (isEmailDuplicated(dto.getUserEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        
        String verifiedKey = "verified:" + dto.getUserEmail();
        boolean verified = verificationCodeStore.verifyCode(verifiedKey, "true");

        if (!verified) {
        	log.warn("이메일 인증 안된 사용자 가입 시도: {}", dto.getUserEmail());	// TODO: 통계 때문에 넣어둔거임
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
        }

        verificationCodeStore.remove(verifiedKey); // 인증 한 번만 사용하도록 제거
        
        if (isNicknameDuplicated(dto.getUserNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        
        // 권한
        CommonCode role = commonCodeRepository.findById("ROLE_NORMAL")
            .orElseThrow(() -> new IllegalStateException("기본 권한 코드 없음"));
        
        // 상태
        CommonCode status = commonCodeRepository.findById("STATUS_ACTIVE")
            .orElseThrow(() -> new IllegalStateException("기본 상태 코드 없음"));
    	
        Users user = Users.builder()
                .userEmail(dto.getUserEmail())
                .userPassword(passwordEncoder.encode(dto.getUserPassword()))
                .userName(dto.getUserName())
                .userNickname(dto.getUserNickname())
                .userTel(dto.getUserTel())
                .userBirth(dto.getUserBirth())
                .userZipcode(dto.getUserZipcode())
                .userStreet1(dto.getUserStreet1())
                .userDetailAddress(dto.getUserDetailAddress())
                .userHasPet(false)
                .userEmailChecked(dto.getUserEmailChecked())
                .userBlacklist(false)
                .userCreatedAt(LocalDateTime.now())
                .roleCode(role)
                .statusCode(status)
                .build();

        userRepository.save(user);
    }
    
    // 중복확인
    public boolean isEmailDuplicated(String email) {
    	return userRepository.existsByUserEmail(email);
    }

    // 중복확인
    public boolean isNicknameDuplicated(String nickname) {
    	return userRepository.existsByUserNickname(nickname);
    }
}