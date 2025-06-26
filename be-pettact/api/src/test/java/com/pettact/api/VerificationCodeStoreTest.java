package com.pettact.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.pettact.api.verification.VerificationCodeStore;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class VerificationCodeStoreTest {
    
    @Autowired
    private VerificationCodeStore store;
    
    @Test
    @Order(1)
    void 코드_저장_테스트() {
        store.saveCode("test@email.com", "123456");
        String code = store.getCode("test@email.com");
        assertEquals("123456", code);
    }
    
//    @Test
//    @Order(2)
//    void 코드_검증_테스트() {
//        store.saveCode("verify@email.com", "abc123");
//        assertTrue(store.verifyCode("verify@email.com", "abc123"));
//        assertFalse(store.verifyCode("verify@email.com", "wrong"));
//    }
//    
//    @Test
//    @Order(3)
//    void TTL_테스트() throws InterruptedException {
//        store.saveCode("ttl@email.com", "temp123");
//        
//        // 즉시 확인 - 존재해야 함
//        assertNotNull(store.getCode("ttl@email.com"));
//        
//        // 5분 1초 대기 (실제로는 짧게 설정해서 테스트)
//        Thread.sleep(6000); // 6초 대기
//        
//        // 만료 확인
//        assertNull(store.getCode("ttl@email.com"));
//    }
}
