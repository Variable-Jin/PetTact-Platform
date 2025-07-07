package com.pettact.api.notification.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.notification.dto.NotificationReqDTO;
import com.pettact.api.notification.dto.NotificationResDTO;
import com.pettact.api.notification.entity.Notification;
import com.pettact.api.notification.service.NotificationService;
import com.pettact.api.security.vo.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // TODO: 일반 사용자 접근 제한 , 예외처리
    @PostMapping
    public String sendNotification(@RequestBody NotificationReqDTO dto,
                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long senderUserNo = userDetails.getUserEntity().getUserNo();
        dto.setSenderUserNo(senderUserNo);
        notificationService.sendNotification(dto);
        return "Notification sent successfully!";
    }
    
    // 사용자 알림 목록 조회
    @GetMapping
    public ResponseEntity<List<NotificationResDTO>> getMyNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userNo = userDetails.getUserEntity().getUserNo();
        log.info("userDetails: {}", userDetails);
        List<NotificationResDTO> list = notificationService.getNotificationsByUser(userNo);
        log.info("list: {}", list);
        return ResponseEntity.ok().body(list);
    }
    
    // 읽지 않은 알림 개수 조회
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadNotificationCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            log.error("userDetails is null!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userNo = userDetails.getUserEntity().getUserNo();
        Long count = notificationService.getUnreadNotificationCount(userNo);
        return ResponseEntity.ok().body(count);
    }

    // 특정 알림 읽음 처리
    @PostMapping("/{notificationNo}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable("notificationNo") Long notificationNo) {
        notificationService.markAsRead(notificationNo);
        return ResponseEntity.ok().build();
    }

    // 전체 알림 읽음 처리
    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userNo = userDetails.getUserEntity().getUserNo();
        notificationService.markAllAsRead(userNo);
        return ResponseEntity.ok().build();
    }
    
}
