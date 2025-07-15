package com.pettact.api.notification.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

import com.pettact.api.board.entity.Board;
import com.pettact.api.code.entity.CommonCode;
import com.pettact.api.core.base.BaseEntity;
import com.pettact.api.notification.dto.NotificationReqDTO;
import com.pettact.api.notification.enums.NotificationType;
import com.pettact.api.notification.enums.TargetType;
import com.pettact.api.reply.entity.Reply;
import com.pettact.api.user.entity.Users;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_no")
    private Long notificationNo;

    @Column(name = "sender_user_no")
    private Long senderUserNo;

    @Column(name = "receiver_user_no", nullable = false)
    private Long receiverUserNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Column(name = "notification_title", nullable = false)
    private String notificationTitle;

    @Column(name = "notification_content", nullable = false)
    private String notificationContent;

    @Column(name = "target_id")
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type")
    private TargetType targetType;

    @Column(name = "is_read", nullable = false)
    @ColumnDefault("false")
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
    
    public static Notification from(NotificationReqDTO dto) {
        return Notification.builder()
                .senderUserNo(dto.getSenderUserNo())
                .receiverUserNo(dto.getReceiverUserNo())
                .notificationType(dto.getNotificationType())
                .notificationTitle(dto.getNotificationTitle())
                .notificationContent(dto.getNotificationContent())
                .targetId(dto.getTargetId())
                .targetType(dto.getTargetType())
                .isRead(false)
                .build();
    }
}

