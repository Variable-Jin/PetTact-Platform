package com.pettact.api.notification;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, Object> objectRedisTemplate;

    public void publish(String channel, String jsonMessage) {
    	objectRedisTemplate.convertAndSend(channel, jsonMessage);
    }
}