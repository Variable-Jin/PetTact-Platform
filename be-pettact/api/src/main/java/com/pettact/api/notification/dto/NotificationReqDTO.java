package com.pettact.api.notification.dto;

import com.pettact.api.notification.enums.NotificationType;
import com.pettact.api.notification.enums.TargetType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationReqDTO {
    private Long senderUserNo;
    private Long receiverUserNo;
    private NotificationType notificationType;
    private String notificationTitle;
    private String notificationContent;
    private Long targetId;
    private TargetType targetType;
    
    public static NotificationReqDTO of(Long senderUserNo, Long receiverUserNo, NotificationType type,
    							Long targetId, TargetType targetType, String title, String content) {
			return NotificationReqDTO.builder()
				.senderUserNo(senderUserNo)
				.receiverUserNo(receiverUserNo)
				.notificationType(type)
				.notificationTitle(title)
				.notificationContent(content)
				.targetId(targetId)
				.targetType(targetType)
				.build();
}
}
