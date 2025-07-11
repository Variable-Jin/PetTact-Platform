package com.pettact.api.websocket.subscriber;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActiveUsersSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String msgBody = new String(message.getBody());
            log.info("Received active user count from Redis: {}", msgBody);

            // msgBody가 단순 숫자라 가정
            int activeUserCount = Integer.parseInt(msgBody);

            messagingTemplate.convertAndSend("/topic/activeUsers", activeUserCount);

        } catch (Exception e) {
            log.error("Failed to process active users message", e);
        }
    }
}
