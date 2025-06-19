package com.pettact.api.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pettact.api.user.dto.UserJoinDTO;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
    public void join(UserJoinDTO dto) {

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
                .userHasPet(dto.getHasPet())
                .userEmailChecked(dto.getEmailChecked())
                .roleCode(null)
                .statusCode(null)
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
