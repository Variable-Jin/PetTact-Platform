package com.pettact.api.notification.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pettact.api.notification.entity.Notification;
import com.pettact.api.notification.enums.NotificationType;
import com.pettact.api.notification.enums.TargetType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResDTO {
    private Long notificationNo;
    private Long senderUserNo;
    private Long receiverUserNo;
    private NotificationType notificationType;
    private String notificationTitle;
    private String notificationContent;
    private Long targetId;
    private TargetType targetType;
    @JsonProperty("isRead")
    private boolean isRead;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
    private LocalDateTime readAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
    private LocalDateTime createdAt;

    public static NotificationResDTO from(Notification notification) {
        return NotificationResDTO.builder()
                .notificationNo(notification.getNotificationNo())
                .senderUserNo(notification.getSenderUserNo())
                .receiverUserNo(notification.getReceiverUserNo())
                .notificationType(notification.getNotificationType())
                .notificationTitle(notification.getNotificationTitle())
                .notificationContent(notification.getNotificationContent())
                .targetId(notification.getTargetId())
                .targetType(notification.getTargetType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}


