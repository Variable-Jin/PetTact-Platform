package com.pettact.api.common.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pettact.api.board.service.BoardService;
import com.pettact.api.pet.service.PetAdminService;
import com.pettact.api.product.service.ProductService;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewCountScheduler {
	/*
	 * pet , board , product 조회수 db 업데이ㅌ트
	 */
	
    private final StringRedisTemplate redisTemplate;
    private final PetAdminService petService;
    private final BoardService boardService;
//    private final ProductService productService;

    @Scheduled(cron = "0 */10 * * * *") // 배치 주기 10분 - 초 분 시 일 월 요일
    public void syncViewCounts() {
    	syncDomainViewCounts("pet", petService, String.class);
        syncDomainViewCounts("board", boardService, Long.class);
//        syncDomainViewCounts("product", productService, Long.class);
    }

    private <T> void syncDomainViewCounts(String domainPrefix, ViewCountSyncable<T> service, Class<T> clazz) {
        String pattern = domainPrefix + ":views:*";
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys == null || keys.isEmpty()) {
            log.info("[{}] 조회수 반영할 key 없음", domainPrefix);
            return;
        }

        for (String key : keys) {
            String idStr = key.substring(key.lastIndexOf(":") + 1);
            T id = parseId(idStr, clazz);

            String countStr = redisTemplate.opsForValue().get(key);
            int count = (countStr != null) ? Integer.parseInt(countStr) : 0;

            service.updateViewCount(id, count);
//            redisTemplate.delete(key);
        }

        log.info("[{}] 조회수 {}건 DB 반영 완료", domainPrefix, keys.size());
    }
    
    private <T> T parseId(String idStr, Class<T> clazz) {
        if (clazz == Long.class) {
            return clazz.cast(Long.valueOf(idStr));
        } else if (clazz == String.class) {
            return clazz.cast(idStr);
        } else {
            throw new IllegalArgumentException("지원하지 않는 ID 타입: " + clazz.getName());
        }
    }
    
    // 각 도메인 서비스에서 구현할 인터페이스
    public interface ViewCountSyncable<T> {
        void updateViewCount(T id, int count);
    }
}
