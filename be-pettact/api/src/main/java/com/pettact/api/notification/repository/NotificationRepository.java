package com.pettact.api.notification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pettact.api.notification.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
//  특정 사용자의 모든 알림 조회 (최신순)
	List<Notification> findByReceiverUserNoOrderByCreatedAtDesc(Long receiverUserNo);

//	특정 사용자의 읽지 않은 알림 개수
	long countByReceiverUserNoAndIsReadFalse(Long receiverUserNo);
	
	
	
    /**
     * 특정 사용자의 읽지 않은 알림 조회 (최신순)
     */
    List<Notification> findByReceiverUserNoAndIsReadFalseOrderByCreatedAtDesc(Long receiverUserNo);
    
    /**
     * 특정 사용자의 읽지 않은 알림 조회
     */
    List<Notification> findByReceiverUserNoAndIsReadFalse(Long receiverUserNo);
    
    /**
     * 특정 알림 조회 (사용자 확인 포함)
     */
    Optional<Notification> findByNotificationNoAndReceiverUserNo(Long notificationNo, Long receiverUserNo);
    
}