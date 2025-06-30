package com.pettact.api.notification;

import java.nio.charset.StandardCharsets;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pettact.api.notification.dto.NotificationResDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(org.springframework.data.redis.connection.Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            NotificationResDTO dto = objectMapper.readValue(json, NotificationResDTO.class);

            // /user/{userId}/queue/notifications 경로로 전송
            messagingTemplate.convertAndSendToUser(
                    dto.getReceiverUserNo().toString(),
                    "/queue/notifications",
                    dto
            );
        } catch (Exception e) {
            log.error("RedisSubscriber onMessage error", e);
        }
    }
}

