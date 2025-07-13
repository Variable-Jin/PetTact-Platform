package com.pettact.api.notification.publisher;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.pettact.api.notification.dto.NotificationResDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, NotificationResDTO> notificationRedisTemplate;

    public void publish(String topic, NotificationResDTO notification) {
        notificationRedisTemplate.convertAndSend(topic, notification);
    }
}

