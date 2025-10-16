package com.pettact.api.verification;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VerificationCodeStore {
    
	@Qualifier("customStringRedisTemplate")
    private final RedisTemplate<String, String> redisTemplate;
	
    private static final String PREFIX = "verification:";
    private static final int EXPIRE_MINUTES = 5;

    // 코드 저장 (5분)
    public void saveCode(String key, String code) {
    	String redisKey = PREFIX + key;
    	redisTemplate.opsForValue().set(redisKey, code, EXPIRE_MINUTES, TimeUnit.MINUTES);
    }

    // 코드 직접 검증
    public boolean verifyCode(String key, String inputCode) {
    	String redisKey = PREFIX + key;
    	String storeCode = redisTemplate.opsForValue().get(redisKey);
    	
    	if(storeCode == null) {
    		return false;
    	}
    	
        return storeCode.equals(inputCode);
    }

    // 저장된 값 직접 가져오기 (링크 인증 -> 토큰에서 이메일(key)를 꺼내야함)
    public String getCode(String key) {
    	String redisKey = PREFIX + key;
    	return redisTemplate.opsForValue().get(redisKey);
    }

    // 인증 완료되면 키 삭제
    public void remove(String key) {
        String redisKey = PREFIX + key;
        redisTemplate.delete(redisKey);
    }
}
