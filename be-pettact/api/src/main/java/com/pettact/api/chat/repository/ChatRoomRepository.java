package com.pettact.api.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pettact.api.chat.dto.ChatRoomDTO;
import com.pettact.api.chat.entity.ChatRoom;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

	Optional<ChatRoom> findByRoomNo(Long roomNo);
	
	// 로그인한 사용자의 리스트
	@Query("SELECT new com.pettact.api.chat.dto.ChatRoomDTO(c.roomNo, c.name) " +
		       "FROM ChatRoom c " +
		       "WHERE c.user1.userNo = :userNo OR c.user2.userNo = :userNo")
    List<ChatRoomDTO> findRoomListByUserNo(@Param("userNo") Long userNo);
	// 두 사용자 채팅방이 이미 존재하는지 확인 
	@Query("SELECT c FROM ChatRoom c " +
		       "WHERE (c.user1.userNo = :user1 AND c.user2.userNo = :user2) " +
		       "   OR (c.user1.userNo = :user2 AND c.user2.userNo = :user1)")
    Optional<ChatRoom> findRoomByUsers(@Param("user1") Long user1No, @Param("user2") Long user2No);
}
