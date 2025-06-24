package com.pettact.api.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pettact.api.admin.dto.AdminUserDTO;
import com.pettact.api.code.entity.CommonCode;
import com.pettact.api.user.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {
	Optional<Users> findByUserEmail(String userEmail);
	
	boolean existsByUserEmail(String userEmail);
	
	boolean existsByUserNickname(String userNickname);

	Optional<Users> findByUserNameAndUserTel(String userName, String userTel);
	
	List<Users> findByIsDeletedFalse();
	
	// admin
	// 회원 목록 조회
	List<AdminUserDTO> findAllOrderByCreadtedAtDesc();
	
	// status_code별 회원 조회(status_blocked - 정지, status_pending - 승인대기)
	List<Users> findByStatusCodeOrderByCreadtedAtDesc();
	
	// 회원 검색(회원 이메일, 이름, 닉네임)
	List<Users> findByContainingUserEmailOrUserNameOrUserNickname(String UserEmail, String UserName, String UserNickname);
	
    // 탈퇴하지 않은 회원만 조회
    List<Users> findByIsDeletedFalseOrderByCreatedAtDesc();
    
    // 블랙리스트 회원 조회
    List<Users> findByUserBlacklistTrueOrderByCreatedAtDesc();
}
