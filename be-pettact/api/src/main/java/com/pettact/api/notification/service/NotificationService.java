package com.pettact.api.notification.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pettact.api.notification.dto.NotificationReqDTO;
import com.pettact.api.notification.dto.NotificationResDTO;
import com.pettact.api.notification.entity.Notification;
import com.pettact.api.notification.repository.NotificationRepository;
import com.pettact.api.websocket.publisher.NotificationPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPublisher notificationPublisher;
    private final ObjectMapper objectMapper;

    public void sendNotification(NotificationReqDTO dto) {
        Notification notification = Notification.from(dto);
        notificationRepository.save(notification);

        try {
            String message = objectMapper.writeValueAsString(notification);
            notificationPublisher.publish("notifications", message);
        } catch (Exception e) {
            throw new RuntimeException("JSON 변환 실패", e);
        }
    }        

    // 특정 사용자의 모든 알림
    @Transactional(readOnly = true)
    public List<NotificationResDTO> getNotificationsByUser(Long userNo) {
        List<Notification> notifications = notificationRepository.findByReceiverUserNoOrderByCreatedAtDesc(userNo);
        return notifications.stream()
                .map(NotificationResDTO::from)
                .toList();
    }

    // 읽지 않은 알림 개수 조회
    @Transactional(readOnly = true)
    public Long getUnreadNotificationCount(Long userNo) {
        return notificationRepository.countByReceiverUserNoAndIsReadFalse(userNo);
    }

    // 특정 알림 읽음 처리
    @Transactional
    public void markAsRead(Long notificationNo) {
        Notification notification = notificationRepository.findById(notificationNo)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        if (!notification.getIsRead()) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    // 모든 알림 읽음 처리
    @Transactional
    public void markAllAsRead(Long userNo) {
        List<Notification> notifications = notificationRepository.findByReceiverUserNoOrderByCreatedAtDesc(userNo);
        for (Notification notification : notifications) {
            if (!notification.getIsRead()) {
                notification.setIsRead(true);
                notification.setReadAt(LocalDateTime.now());
            }
        }
        notificationRepository.saveAll(notifications);
    }
}