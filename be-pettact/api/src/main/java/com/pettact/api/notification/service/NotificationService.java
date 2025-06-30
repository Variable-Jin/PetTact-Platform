package com.pettact.api.notification.service;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pettact.api.config.RedisChannel;
import com.pettact.api.notification.RedisPublisher;
import com.pettact.api.notification.RedisSubscriber;
import com.pettact.api.notification.dto.NotificationReqDTO;
import com.pettact.api.notification.dto.NotificationResDTO;
import com.pettact.api.notification.entity.Notification;
import com.pettact.api.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
	private final NotificationRepository notificationRepository;
	private final RedisPublisher redisPublisher;
	private final ObjectMapper objectMapper;
	
	// 알림 생성
	public void createNotification(NotificationReqDTO reqDTO) {
		Notification notification = Notification.from(reqDTO);
		
		notificationRepository.save(notification);
		
		// Redis
        try {
            // 알림을 JSON으로 직렬화하여 Redis에 발행
            String json = objectMapper.writeValueAsString(NotificationResDTO.from(notification));
            redisPublisher.publish(RedisChannel.NOTIFICATION, json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("notification JSON 직렬화 실패");
        }
	}

	// 알림 목록
	public List<NotificationResDTO> getNotificationsByUser(Long userNo){
		List<Notification> notifications = notificationRepository.findByReceiverUserNoOrderByNotificationNoDesc(userNo);
		
		return notifications.stream()
				.map(NotificationResDTO::from)
				.toList();
	};
	
	// 읽지 않음 알림 수 조회
	public Long countUnreadNotifications(Long userNo) {
		return notificationRepository.countByReceiverUserNoAndIsReadFalse(userNo);
	};
		
	// 읽음 처리
	public void markAsRead(Long notificationNo) {
	    Notification notification = notificationRepository.findById(notificationNo)
	            .orElseThrow(() -> new IllegalArgumentException("해당 알림이 존재하지 않습니다."));
	    
	    if(!notification.getIsRead()) {
	    	notification.setIsRead(true);
	    	notificationRepository.save(notification);
	    }
	};
}
