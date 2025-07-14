package com.pettact.api.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pettact.api.notification.dto.NotificationResDTO;
import com.pettact.api.notification.subscriber.RedisSubscriber;
import com.pettact.api.pet.dto.PetAbandonmentDto;

@Configuration
public class RedisConfig {

	// 실시간 알림 연결용
	@Bean
	public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
	                                                    RedisSubscriber redisSubscriber
    ) {
	    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
	    container.setConnectionFactory(connectionFactory);
	    container.addMessageListener(redisSubscriber, new PatternTopic("notifications"));
	    return container;
	}
	
	@Bean
	public RedisTemplate<String, NotificationResDTO> notificationRedisTemplate(
	    RedisConnectionFactory connectionFactory,
	    ObjectMapper objectMapper
	) {
	    RedisTemplate<String, NotificationResDTO> template = new RedisTemplate<>();
	    template.setConnectionFactory(connectionFactory);

	    Jackson2JsonRedisSerializer<NotificationResDTO> serializer =
	        new Jackson2JsonRedisSerializer<>(NotificationResDTO.class);
	    serializer.setObjectMapper(objectMapper); // ✅ 이거 중요

	    template.setKeySerializer(new StringRedisSerializer());
	    template.setValueSerializer(serializer);
	    return template;
	}
	
	@Bean
	public ObjectMapper objectMapper() {
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.registerModule(new JavaTimeModule());
	    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	    return mapper;
	}

	// =================== 캐싱 ===================
    // Redis의 key-value 데이터를 쉽게 다루기 위한 Bean
    // RedisTemplate Bean 등록
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
	    RedisTemplate<String, Object> template = new RedisTemplate<>();
	    template.setConnectionFactory(connectionFactory);

	    // Key Serializer
	    template.setKeySerializer(new StringRedisSerializer());

	    // Value Serializer: GenericJackson2JsonRedisSerializer
	    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
	    template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

	    template.afterPropertiesSet();
	    return template;
	}

    /* List<PetAbandonmentDto> 전용 RedisTemplate */
    @Bean
    public RedisTemplate<String, List<PetAbandonmentDto>> petAbandonmentDtoListRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, List<PetAbandonmentDto>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
	
    // viewCountService에서 사용
	@Bean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
	    return new StringRedisTemplate(connectionFactory);
	}
	
    // String key-value 타입 (조회수 INCR 시 사용)
    @Bean
    public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    // Sorted Set(ZSET) 타입 (인기글 랭킹 ZINCRBY 시 사용)
    @Bean
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }

}
