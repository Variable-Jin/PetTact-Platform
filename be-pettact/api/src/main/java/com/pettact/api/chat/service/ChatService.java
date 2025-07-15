package com.pettact.api.chat.service;

import com.pettact.api.admin.dto.user.AdminUserDetailDTO;
import com.pettact.api.chat.dto.ChatMessageDTO;
import com.pettact.api.chat.dto.ChatRoomDTO;
import com.pettact.api.chat.entity.ChatMessageEntity;
import com.pettact.api.chat.entity.ChatReadEntity;
import com.pettact.api.chat.entity.ChatRoomEntity;
import com.pettact.api.chat.repository.ChatMessageRepository;
import com.pettact.api.chat.repository.ChatReadRepository;
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
    private final ChatReadRepository chatReadRepository;
    private final UserRepository userRepository;

    public void handleIncomingMessage(ChatMessageDTO messageDTO, Map<String, Object> sessionAttributes) {
        Long userNo = (Long) sessionAttributes.get("userNo");

        if (userNo == null) {
            throw new RuntimeException("유저 인증 정보가 세션에 없습니다.");
        }

        Users userData = userRepository.findById(userNo)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        ChatRoomEntity room = chatRoomRepository.findById(messageDTO.getRoomNo())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 채팅방입니다."));

        ChatMessageEntity entity = ChatMessageEntity.builder()
                .chatRoom(room) 
                .sender(userData)
                .message(messageDTO.getMessage())
                .build();

        chatMessageRepository.save(entity);
        messageDTO.setMessageId(entity.getChatMessageNo()); 
        messageDTO.setSenderNickname(userData.getUserNickname());
        messageDTO.setSenderUserNo(userNo);
        messagingTemplate.convertAndSend("/topic/chat/room/" + room.getRoomNo(), messageDTO);
    }

    public List<ChatRoomDTO> getMyChatRooms(CustomUserDetails user) {
        Long userNo = user.getUserEntity().getUserNo();
        return chatRoomRepository.findRoomListByUserNo(userNo);
    }

    public List<ChatMessageDTO> getMessagesByRoom(Long roomNo, Long userNo) {
        List<ChatMessageEntity> messages = chatMessageRepository.findByChatRoomRoomNoOrderByCreatedAtAsc(roomNo);

        return messages.stream().map(m -> ChatMessageDTO.builder()
                .messageId(m.getChatMessageNo())
                .roomNo(m.getChatRoom().getRoomNo())
                .senderUserNo(m.getSender().getUserNo())
                .senderNickname(m.getSender().getUserNickname())
                .message(m.getMessage())
                .build())
            .collect(Collectors.toList());
    }

    public ResponseEntity<?> createRoom(CustomUserDetails users1, Long user2No) {
        Long user1No = users1.getUserEntity().getUserNo();

        if (user1No.equals(user2No)) {
            throw new IllegalArgumentException("자기 자신과는 채팅할 수 없습니다.");
        }

        Optional<ChatRoomEntity> existing = chatRoomRepository.findRoomByUsers(user1No, user2No);
        if (existing.isPresent()) {
            ChatRoomEntity room = existing.get();
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

        ChatRoomEntity newRoom = new ChatRoomEntity();
        newRoom.setUser1(user1);
        newRoom.setUser2(user2);
        newRoom.setName(user1.getUserNickname() + " & " + user2.getUserNickname());

        ChatRoomEntity saved = chatRoomRepository.save(newRoom);

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

    public void updateLastRead(Long userNo, Long roomNo, Long lastMessageId) {
        if (lastMessageId == null) {
            log.warn("lastMessageId가 null입니다. 읽음 처리 생략 - userNo={}, roomNo={}", userNo, roomNo);
            return;
        }
        ChatReadEntity read = chatReadRepository.findByRoomNoAndUserNo(roomNo, userNo)
            .orElse(new ChatReadEntity(roomNo, userNo, 0L));
        if (lastMessageId > read.getLastReadMessageNo()) {
            read.setLastReadMessageNo(lastMessageId);
            chatReadRepository.save(read);
        }
    }

    public ResponseEntity<?> getUserDetailByNickname(String nickname) {
        Users user = userRepository.findByUserNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("해당 닉네임의 회원을 찾을 수 없습니다."));
        return ResponseEntity.ok(AdminUserDetailDTO.from(user));
    }
}
