package com.pettact.api.chat.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.chat.entity.ChatRoom;
import com.pettact.api.chat.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomRestController {

    private final ChatRoomRepository chatRoomRepository;

    // 채팅방 생성
    @PostMapping
    public ChatRoom createRoom(@RequestParam String name) {
        ChatRoom room = new ChatRoom();
        room.setName(name);
        // 방 번호는 DB에서 자동 생성되거나 클라이언트가 지정해야 합니다.
        return chatRoomRepository.save(room);
    }
    // 모든 채팅방 조회
    @GetMapping
    public List<ChatRoom> findAllRooms() {
        return chatRoomRepository.findAll();
    }

    // 특정 채팅방 조회
    @GetMapping("/{roomNo}")
    public ChatRoom findRoom(@PathVariable("roomNo") Long roomNo) {
        return chatRoomRepository.findById(roomNo)
                .orElseThrow(() -> new RuntimeException("해당 채팅방이 존재하지 않습니다."));
    }
}
