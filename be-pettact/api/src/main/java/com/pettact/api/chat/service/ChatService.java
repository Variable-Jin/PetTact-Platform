package com.pettact.api.chat.service;

import com.pettact.api.admin.dto.user.AdminUserDetailDTO;
import com.pettact.api.chat.dto.ChatMessageDTO;
import com.pettact.api.chat.dto.ChatRoomDTO;
import com.pettact.api.chat.entity.ChatMessage;
import com.pettact.api.chat.entity.ChatRoom;
import com.pettact.api.chat.repository.ChatMessageRepository;
import com.pettact.api.chat.repository.ChatRoomRepository;
import com.pettact.api.security.vo.CustomUserDetails;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public void handleIncomingMessage(ChatMessageDTO messageDTO, Map<String, Object> sessionAttributes) {
        Long userNo = (Long) sessionAttributes.get("userNo");

        if (userNo == null) {
            throw new RuntimeException("유저 인증 정보가 세션에 없습니다.");
        }

        Users userData = userRepository.findById(userNo)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        log.info("채팅 수신 → 방번호: {}, 보낸 사람: {}, 내용: {}",
                messageDTO.getRoomNo(), userNo, messageDTO.getMessage());

        ChatRoom room = chatRoomRepository.findById(messageDTO.getRoomNo())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 채팅방입니다."));

        ChatMessage entity = ChatMessage.builder()
                .roomNo(room.getRoomNo())
                .sender(userData)
                .message(messageDTO.getMessage())
                .build();

        chatMessageRepository.save(entity);
        messageDTO.setSenderNickname(userData.getUserNickname()); 
        messageDTO.setSenderUserNo(userNo);
        messageDTO.setIsMine(true);
        messagingTemplate.convertAndSend("/topic/chat/room/" + room.getRoomNo(), messageDTO);
    }

    public List<ChatRoomDTO> getMyChatRooms(CustomUserDetails user) {
        Long userNo = user.getUserEntity().getUserNo();
        return chatRoomRepository.findRoomListByUserNo(userNo);
    }

    public List<ChatMessageDTO> getMessagesByRoom(Long roomNo, Long userNo) {
        List<ChatMessage> messages = chatMessageRepository.findByRoomNoOrderByCreatedAtAsc(roomNo);

        return messages.stream().map(m -> ChatMessageDTO.builder()
                .messageId(m.getChatMessageNo())
                .roomNo(m.getRoomNo())
                .senderUserNo(m.getSender().getUserNo())
                .senderNickname(m.getSender().getUserNickname())
                .message(m.getMessage())
                .isMine(m.getSender().getUserNo().equals(userNo))
                .build())
            .collect(Collectors.toList());
    }

    public ResponseEntity<?> createRoom(CustomUserDetails users1, Long user2No) {
        Long user1No = users1.getUserEntity().getUserNo();

        if (user1No.equals(user2No)) {
            throw new IllegalArgumentException("자기 자신과는 채팅할 수 없습니다.");
        }

        Optional<ChatRoom> existing = chatRoomRepository.findRoomByUsers(user1No, user2No);
        if (existing.isPresent()) {
            ChatRoom room = existing.get();
            return ResponseEntity.ok(ChatRoomDTO.builder()
                    .roomNo(room.getRoomNo())
                    .name(room.getName())
                    .user1No(room.getUser1().getUserNo())
                    .user1Nickname(room.getUser1().getUserNickname())
                    .user2No(room.getUser2().getUserNo())
                    .user2Nickname(room.getUser2().getUserNickname())
                    .build());
        }

        Users user1 = userRepository.findById(user1No)
                .orElseThrow(() -> new RuntimeException("보낸 사용자 없음"));
        Users user2 = userRepository.findById(user2No)
                .orElseThrow(() -> new RuntimeException("받는 사용자 없음"));

        ChatRoom newRoom = new ChatRoom();
        newRoom.setUser1(user1);
        newRoom.setUser2(user2);
        newRoom.setName(user1.getUserNickname() + " & " + user2.getUserNickname());

        ChatRoom saved = chatRoomRepository.save(newRoom);

        ChatRoomDTO result = ChatRoomDTO.builder()
                .roomNo(saved.getRoomNo())
                .name(saved.getName())
                .user1No(user1.getUserNo())
                .user1Nickname(user1.getUserNickname())
                .user2No(user2.getUserNo())
                .user2Nickname(user2.getUserNickname())
                .build();

        return ResponseEntity.ok(result);
    }

    public ResponseEntity<?> getUserDetailByNickname(String nickname) {
        Users user = userRepository.findByUserNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("해당 닉네임의 회원을 찾을 수 없습니다."));
        return ResponseEntity.ok(AdminUserDetailDTO.from(user));
    }
}