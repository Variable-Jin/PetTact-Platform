package com.pettact.api.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pettact.api.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>{
	List<Notification> findByReceiverUserNoOrderByNotificationNoDesc(Long receiverUserNo);
	long countByReceiverUserNoAndIsReadFalse(Long receiverUserNo);
	
	
}
