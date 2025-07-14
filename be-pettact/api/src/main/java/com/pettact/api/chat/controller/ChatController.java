package com.pettact.api.chat.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.chat.dto.ChatMessageDTO;
import com.pettact.api.chat.dto.ChatRoomDTO;
import com.pettact.api.chat.service.ChatService;
import com.pettact.api.security.vo.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Controller // WebSocket과 RestController 병행
@RequestMapping("/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void handleChatMessage(@Payload ChatMessageDTO messageDTO,
                                  @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {
        chatService.handleIncomingMessage(messageDTO, sessionAttributes);
    }

    @GetMapping("/room/my")
    public List<ChatRoomDTO> getMyChatRooms(@AuthenticationPrincipal CustomUserDetails user) {
        return chatService.getMyChatRooms(user);
    }

    @GetMapping("/message/{roomNo}")
    public List<ChatMessageDTO> getMessagesByRoom(@PathVariable("roomNo") Long roomNo, @AuthenticationPrincipal CustomUserDetails user) {
    	return chatService.getMessagesByRoom(roomNo, user.getUserEntity().getUserNo());
    }

    @PostMapping("/room/create/{targetUserNo}")
    public ResponseEntity<?> createRoom(@AuthenticationPrincipal CustomUserDetails users1,
                                                  @PathVariable("targetUserNo") Long user2No) {
        return chatService.createRoom(users1, user2No);
    }

    @GetMapping("/user/{nickName}")
    public ResponseEntity<?> getUserDetailByNickname(@PathVariable("nickName") String nickname) {
        return chatService.getUserDetailByNickname(nickname);
    }
}