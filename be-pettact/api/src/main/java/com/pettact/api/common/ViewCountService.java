package com.pettact.api.common;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ViewCountService {
	
	private final StringRedisTemplate redisTemplate;
	
    /*
     * 조회수 증가
     * @param domainPrefix  (ex) pet, board, product 등
     * @param id  (ex) entity id
     * @param ttlMinutes  (ex) TTL 설정 시간 (분)
     * @return 증가된 후 조회수(Long)
     */
    public Long increaseViewCount(String domainPrefix, Object id, long ttlMinutes) {
        String key = domainPrefix + ":views:" + String.valueOf(id);

        Long views = redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, Duration.ofMinutes(ttlMinutes));

        return views;
    }
}
