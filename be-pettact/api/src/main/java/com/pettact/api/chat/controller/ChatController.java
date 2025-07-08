package com.pettact.api.chat.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.pettact.api.chat.dto.ChatMessageDTO;
import com.pettact.api.chat.entity.ChatMessage;
import com.pettact.api.chat.entity.ChatRoom;
import com.pettact.api.chat.repository.ChatMessageRepository;
import com.pettact.api.chat.repository.ChatRoomRepository;
import com.pettact.api.security.vo.UserPrincipal;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository; // 🔥 추가

    @MessageMapping("/chat/message")
    public void handleChatMessage(@Payload ChatMessageDTO messageDTO, @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {
    	UserPrincipal principal = (UserPrincipal) sessionAttributes.get("principal");
        if (principal == null) {
            log.error("Principal is null. Unauthorized access attempt.");
            throw new RuntimeException("Unauthorized");
        }

        // 🔥 userNo(Long)로 변환
        Long userNo = Long.parseLong(principal.getName());

        // 🔥 DB에서 userNo로 UserEntity 조회
        Users user = userRepository.findById(userNo)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        String userNickname = user.getUserNickname();
        String userEmail = user.getUserEmail();

        log.info("채팅 수신 → 방번호: {}, 보낸 사람: {}({}), 내용: {}",
                messageDTO.getRoomNo(), userNickname, userEmail, messageDTO.getMessage());

        // 채팅방 존재 여부 확인
        ChatRoom room = chatRoomRepository.findById(messageDTO.getRoomNo())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 채팅방입니다."));

        // 메시지 엔티티 저장
        ChatMessage entity = ChatMessage.builder()
                .roomNo(room.getRoomNo())
                .sender(userNickname) // sender를 nickname으로 설정
                .message(messageDTO.getMessage())
                .build();

        chatMessageRepository.save(entity);

        // 메시지 DTO에 서버에서 sender 세팅
        messageDTO.setSender(userNickname);

        // 클라이언트로 메시지 브로드캐스트
        messagingTemplate.convertAndSend("/topic/chat/room/" + room.getRoomNo(), messageDTO);
    }
}
