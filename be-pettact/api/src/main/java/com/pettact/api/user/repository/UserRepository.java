package com.pettact.api.user.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pettact.api.user.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {
	Optional<Users> findByUserEmail(String userEmail);
	// 이메일 찾기
	Optional<Users> findByUserNameAndUserTel(String userName, String userTel);
	
	// 회원가입 중복 확인용
	boolean existsByUserEmail(String userEmail);
	boolean existsByUserNickname(String userNickname);
	
	List<Users> findByIsDeletedFalse();
	
	// admin
	// 회원 목록 조회
	List<Users> findAllByOrderByCreatedAtDesc();
	
	// 회원 검색(회원 이메일, 이름, 닉네임) , 필터링(status_code, role_code, 날짜 로)
	@Query("""
			SELECT u FROM Users u
			WHERE (:keyword IS NULL OR u.userNickname LIKE CONCAT('%', :keyword, '%')
				OR u.userEmail LIKE CONCAT('%', :keyword, '%')
				OR u.userName LIKE CONCAT('%', :keyword, '%'))
			AND (:status IS NULL OR u.statusCode.codeId = :status)
			AND (:role IS NULL OR u.roleCode.codeId = :role)
			AND (:startDate IS NULL OR u.createdAt >= :startDate)
			AND (:endDate IS NULL OR u.createdAt <= :endDate)
			AND u.isDeleted = false
			ORDER BY u.createdAt DESC
			""")
	List<Users> findUsersWithFilters(
	    @Param("keyword") String keyword,
	    @Param("status") String status,
	    @Param("role") String role,
	    @Param("startDate") LocalDateTime startDate,
	    @Param("endDate") LocalDateTime endDate
	);

}
