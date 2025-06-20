package com.pettact.api.user.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pettact.api.code.entity.CommonCode;
import com.pettact.api.code.repository.CommonCodeRepository;
import com.pettact.api.user.dto.UserJoinDTO;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final CommonCodeRepository commonCodeRepository;
	private final PasswordEncoder passwordEncoder;
	
    public void join(UserJoinDTO dto) {
        
    	if (isEmailDuplicated(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        
        if (isNicknameDuplicated(dto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        
        // 권한
        CommonCode role = commonCodeRepository.findById("ROLE_NORMAL")
            .orElseThrow(() -> new IllegalStateException("기본 권한 코드 없음"));
        
        // 상태
        CommonCode status = commonCodeRepository.findById("STATUS_ACTIVE")
            .orElseThrow(() -> new IllegalStateException("기본 상태 코드 없음"));
    	
        Users user = Users.builder()
                .userEmail(dto.getEmail())
                .userPassword(passwordEncoder.encode(dto.getPassword()))
                .userName(dto.getName())
                .userNickname(dto.getNickname())
                .userTel(dto.getTel())
                .userBirth(dto.getBirth())
                .userZipcode(dto.getZipcode())
                .userStreet1(dto.getStreet1())
                .userDetailAddress(dto.getDetailAddress())
                .userHasPet(false)
                .userEmailChecked(dto.getEmailChecked())
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