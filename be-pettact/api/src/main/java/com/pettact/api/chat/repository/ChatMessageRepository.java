package com.pettact.api.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pettact.api.chat.entity.ChatMessageEntity;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
	List<ChatMessageEntity> findByChatRoomRoomNoOrderByCreatedAtAsc(Long roomNo);

}

