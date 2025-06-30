package com.pettact.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer{
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws-stomp")
//				.setAllowedOriginPatterns("*") // CORS 설정
				.withSockJS();	// 서버쪽 endpoint랑 클라이언트쪽 endpoint 맞춰줘야함 + sockJS도
	}
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic", "/queue");		// 
		config.setApplicationDestinationPrefixes("/app");	// /app하면 springboot에서 받아서 뭔가 처리할 때
	}
}
