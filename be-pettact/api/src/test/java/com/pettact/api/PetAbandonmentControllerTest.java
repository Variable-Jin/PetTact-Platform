package com.pettact.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class PetAbandonmentControllerTest {
	/*
	 * 조회 수 중복 증가 TEST
	 * 
	 * [기대결과]
	 * 60분 내로는 동일 sessionId 조회수 증가 X
	 * 두번째 호출 후에도, value 1
	 */
	@Autowired
	private MockMvc mockMvc;

    @Autowired
    private StringRedisTemplate redisTemplate;
	
//    @Test
//    void 중복조회_방지_테스트() throws Exception {
//        // given
//        String domainPrefix = "pet";
//        String id = "411300202500238";		// pet - desertionNo
//        String key = domainPrefix + ":views:" + id;
//
//        // 동일 세션 생성
//        MockHttpSession session = new MockHttpSession();
//        
//        // when
//        // 첫 번째 호출
//        mockMvc.perform(get("/v1/api/abandonment/{desertionNo}", id)
//        		.session(session))
//            .andExpect(status().isOk());
//
//        // 두 번째 호출 (같은 세션)
//        mockMvc.perform(get("/v1/api/abandonment/{desertionNo}", id)
//        		.session(session))
//            .andExpect(status().isOk());
//
//        // then
//        String redisValue = redisTemplate.opsForValue().get(key);
//        assertThat(redisValue).isEqualTo("1");
//    }
}
