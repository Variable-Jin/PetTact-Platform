package com.pettact.api.admin.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pettact.api.admin.dto.AdminUserDTO;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
	private final UserRepository userRepository;
	
	// 회원 목록 조회
    public List<AdminUserDTO> getUserList(String keyword, String status, String role,
            							LocalDateTime startDate, LocalDateTime endDate) {

    	List<Users> users = userRepository.findUsersWithFilters(
		(keyword == null || keyword.isBlank()) ? null : keyword,
		(status == null || status.isBlank()) ? null : status,
		(role == null || role.isBlank()) ? null : role,
		startDate,
		endDate
		);
		
		return users.stream()
		.map(AdminUserDTO::from)
		.collect(Collectors.toList());
	}

	// 회원 잠금
	public boolean lockUserByUserNo(Long userNo) {
	    Users user = userRepository.findById(userNo)
	            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

	    if (user.getUserBlacklist()) {
	        return false;
	    }

	    user.setUserBlacklist(true);
	    userRepository.save(user);

	    return true;
	}
	
	// 회원 잠금 해제
	public boolean unlockUserByUserNo(Long userNo) {
	    Users user = userRepository.findById(userNo)
	            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

	    if (!user.getUserBlacklist()) {
	        return false;
	    }

	    user.setUserBlacklist(false);
	    userRepository.save(user);

	    return true;
	}
	
}
