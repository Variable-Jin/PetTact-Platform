package com.pettact.api.websocket.listener;

import com.pettact.api.websocket.publisher.ActiveUsersPublisher;
import com.pettact.api.websocket.service.ActiveUserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final ActiveUserService activeUserService;
    private final ActiveUsersPublisher activeUsersPublisher;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        activeUserService.addSession(sessionId);

        int count = activeUserService.getActiveUserCount();
        activeUsersPublisher.publishActiveUserCount(count);

        log.info("WebSocket Connected: {}, Active Users: {}", sessionId, count);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();

        activeUserService.removeSession(sessionId);

        int count = activeUserService.getActiveUserCount();
        activeUsersPublisher.publishActiveUserCount(count);

        log.info("WebSocket Disconnected: {}, Active Users: {}", sessionId, count);
    }
}
