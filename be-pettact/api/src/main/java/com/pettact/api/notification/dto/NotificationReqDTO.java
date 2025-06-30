package com.pettact.api.notification.dto;

import com.pettact.api.notification.enums.NotificationType;
import com.pettact.api.notification.enums.TargetType;

import lombok.Data;

@Data
public class NotificationReqDTO {
    private Long senderUserNo;
    private Long receiverUserNo;
    private NotificationType notificationType;
    private String notificationTitle;
    private String notificationContent;
    private Long targetId;
    private TargetType targetType;
}
