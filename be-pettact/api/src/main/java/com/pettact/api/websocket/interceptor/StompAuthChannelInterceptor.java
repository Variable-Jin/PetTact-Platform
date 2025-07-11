package com.pettact.api.websocket.interceptor;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.pettact.api.security.util.JwtTokenProvider;
import com.pettact.api.security.vo.UserPrincipal;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.info("STOMP COMMAND = {}", accessor.getCommand());

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
        	log.info("CONNECT 시도됨");
            String tokenHeader = accessor.getFirstNativeHeader("Authorization");
            log.info("CONNECT token header: {}", tokenHeader);

            if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
                log.warn("JWT 토큰이 누락되었거나 Bearer 형식이 아님");
                throw new IllegalArgumentException("No JWT token found in STOMP CONNECT");
            }

            String token = tokenHeader.substring(7);

            if (!jwtTokenProvider.isValidateToken(token)) {
                log.warn("유효하지 않은 JWT 토큰");
                throw new IllegalArgumentException("Invalid JWT token in STOMP CONNECT");
            }

            Map<String, Object> claims = jwtTokenProvider.validateToken(token);
            Long userNo = claims.get("userNo") != null ? Long.parseLong(claims.get("userNo").toString()) : null;
            String userEmail = (String) claims.get("userEmail");

            log.info("STOMP CONNECT 검증 완료 - userNo: {}, userEmail: {}", userNo, userEmail);

            UserPrincipal principal = new UserPrincipal(userNo, userEmail);
            accessor.setUser(principal);

            accessor.getSessionAttributes().put("userNo", userNo);
            accessor.getSessionAttributes().put("userEmail", userEmail);
        }

        return message;
    }
}
