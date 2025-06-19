package com.pettact.api.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pettact.api.user.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {
	Optional<Users> findByUserEmail(String email);
	
	boolean existsByUserEmail(String email);
	
	boolean existsByUserNickname(String nickname);
}
