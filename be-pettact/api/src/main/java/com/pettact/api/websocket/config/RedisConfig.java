package com.pettact.api.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import com.pettact.api.websocket.subscriber.ActiveUsersSubscriber;
import com.pettact.api.websocket.subscriber.NotificationSubscriber;

@Configuration
public class RedisConfig {

	@Bean
	public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
	                                                    NotificationSubscriber notificationSubscriber,
	                                                    ActiveUsersSubscriber activeUsersSubscriber) {
	    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
	    container.setConnectionFactory(connectionFactory);
	    container.addMessageListener(notificationSubscriber, new PatternTopic("notifications"));
	    container.addMessageListener(activeUsersSubscriber, new PatternTopic("activeUsers"));
	    return container;
	}

}
