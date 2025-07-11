package com.pettact.api.websocket.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActiveUsersPublisher {

    private final StringRedisTemplate redisTemplate;

    public void publishActiveUserCount(int count) {
        redisTemplate.convertAndSend("activeUsers", String.valueOf(count));
    }
}
