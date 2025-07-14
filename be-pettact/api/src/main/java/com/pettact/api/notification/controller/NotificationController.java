package com.pettact.api.notification.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.pettact.api.notification.dto.NotificationReqDTO;
import com.pettact.api.notification.dto.NotificationResDTO;
import com.pettact.api.notification.entity.Notification;
import com.pettact.api.notification.service.NotificationService;
import com.pettact.api.notification.service.SseService;
import com.pettact.api.security.util.JwtTokenProvider;
import com.pettact.api.security.vo.CustomUserDetails;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SseService sseService;
    private final JwtTokenProvider jwtTokenProvider;

    // TODO: 일반 사용자 접근 제한 , 예외처리
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@RequestParam("token") String token) {
        Long userNo = jwtTokenProvider.getUserNoFromToken(token); // 👈 여기!
        log.info("[SSE] 사용자 {} SSE 연결 시작", userNo);
        return sseService.connect(userNo);
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
    
    // 알림 삭제
    @PatchMapping("/{notificationNo}/delete")
    public ResponseEntity<?> softDeleteNotification(
            @PathVariable("notificationNo") Long notificationNo,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        notificationService.softDelete(notificationNo, userDetails.getUserEntity().getUserNo());
        return ResponseEntity.ok(Map.of("result", "OK"));
    }
    
    // 알림 전체 삭제
    @PatchMapping("/delete-all")
    public ResponseEntity<?> deleteAllNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.softDeleteAll(userDetails.getUserEntity().getUserNo());
        return ResponseEntity.ok(Map.of("result", "OK"));
    }
    
}
