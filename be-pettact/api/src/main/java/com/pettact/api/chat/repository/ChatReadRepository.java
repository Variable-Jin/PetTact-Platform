package com.pettact.api.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pettact.api.chat.entity.ChatReadEntity;
import com.pettact.api.chat.entity.ChatReadId;

public interface ChatReadRepository extends JpaRepository<ChatReadEntity, ChatReadId> {
    Optional<ChatReadEntity> findByRoomNoAndUserNo(Long roomNo, Long userNo);
}
