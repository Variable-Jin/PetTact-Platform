package com.pettact.api.notification.subscriber;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pettact.api.notification.dto.NotificationResDTO;
import com.pettact.api.notification.entity.Notification;
import com.pettact.api.notification.service.SseService;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SseService sseService;

//    @Override
//    public void onMessage(Message message, byte[] pattern) {
//        try {
//            String body = new String(message.getBody());
//            NotificationResDTO notification = objectMapper.readValue(body, NotificationResDTO.class);
//
//            Long userNo = notification.getReceiverUserNo(); // 대상 사용자
//            sseService.sendToUser(userNo, notification);
//
//        } catch (Exception e) {
//            log.error("RedisSubscriber error", e);
//        }
//    }
    
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody());
            log.info("[SUBSCRIBE] 수신된 메시지: {}", json);
            
            NotificationResDTO notification = objectMapper.readValue(json, NotificationResDTO.class);
            log.info("[REDIS] 수신한 JSON: {}", json);
            
            sseService.sendToUser(notification.getReceiverUserNo(), notification);
        } catch (Exception e) {
            log.error("RedisSubscriber error", e);
        }
    }
}

