package com.pettact.api.websocket.subscriber;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pettact.api.notification.entity.Notification;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String msgBody = new String(message.getBody());
            log.info("Received notification from Redis: {}", msgBody);

            Notification notification = objectMapper.readValue(msgBody, Notification.class);

            String destination = "/queue/notifications/" + notification.getReceiverUserNo();
            messagingTemplate.convertAndSend(destination, notification);

        } catch (Exception e) {
            log.error("Failed to process notification message", e);
        }
    }
}
