package com.pettact.api.websocket.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private final StringRedisTemplate redisTemplate;

    public void publish(String topic, String message) {
        redisTemplate.convertAndSend(topic, message);
    }
}

