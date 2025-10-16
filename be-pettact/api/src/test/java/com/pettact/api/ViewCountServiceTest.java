package com.pettact.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.pettact.api.common.ViewCountService;

@SpringBootTest
class ViewCountServiceTest {
	/*
	 * 조회수 INCR TEST
	 * [기대결과]
	 * 조회수 1씩 증가
	 * 
	 * redis-cli 조회 방법
	 * keys *views* 로 조회
	 * get domainPrefix:views:id
	 */

    @Autowired
    private ViewCountService viewCountService;

    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Test
    void pet_조회수_INCR_테스트() {
        // given
        String domainPrefix = "pet";
        String id = "411300202500238";		// pet - desertionNo
        String key = domainPrefix + ":views:" + id;

        // when
        Long count1 = viewCountService.increaseViewCount(domainPrefix, id, 60);
        Long count2 = viewCountService.increaseViewCount(domainPrefix, id, 60);

        // then
        assertThat(count1).isEqualTo(1L);
        assertThat(count2).isEqualTo(2L);

        String redisValue = redisTemplate.opsForValue().get(key);
        assertThat(redisValue).isEqualTo("2");
        
    }
}
