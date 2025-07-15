package com.pettact.api.notification.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SseService {

    private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();
    private static final Long TIMEOUT = 60L * 1000 * 60; // 1시간 유지

    public SseEmitter connect(Long userNo) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitterMap.put(userNo, emitter);

        emitter.onCompletion(() -> emitterMap.remove(userNo));
        emitter.onTimeout(() -> emitterMap.remove(userNo));
        emitter.onError(e -> emitterMap.remove(userNo));

        return emitter;
    }

    public void sendToUser(Long userNo, Object data) {
    	log.info("[SSE] 사용자 {} 에게 알림 전송 시도: {}", userNo, data);
    	
        SseEmitter emitter = emitterMap.get(userNo);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
            		.name("notification")
            		.data(data));
                log.info("[SSE] 사용자 {} 에게 전송 완료", userNo);
            } catch (IOException e) {
            	log.error("[SSE] 전송 실패: {}", e.getMessage());
                emitter.completeWithError(e);
                emitterMap.remove(userNo);
            }
        } else {
        	log.warn("[SSE] 사용자 {} 에게 연결된 emitter 없음", userNo);
        }
    }
}

