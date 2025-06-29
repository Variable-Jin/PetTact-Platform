package com.pettact.api.notification.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.notification.dto.NotificationReqDTO;
import com.pettact.api.notification.dto.NotificationResDTO;
import com.pettact.api.notification.service.NotificationService;
import com.pettact.api.security.vo.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/notification")
public class NotificationController {
	
	private final NotificationService notificationService;
	
	// TODO: 예외처리
	
	@PostMapping	// TODO: 일반 사용자 접근 제한
	public ResponseEntity<?> createNotification(@RequestBody NotificationReqDTO reqDTO) {
		notificationService.createNotification(reqDTO);
		return ResponseEntity.ok().build();
	}
	
    // 사용자 알림 목록 조회
    @GetMapping
    public ResponseEntity<List<NotificationResDTO>> getMyNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
    	Long userNo = userDetails.getUserEntity().getUserNo();
        List<NotificationResDTO> list = notificationService.getNotificationsByUser(userNo);
        log.info("userDetails: {}", userDetails);
        log.info("list: {}", list);
        return ResponseEntity.ok().body(list);
    }

    // 읽지 않음 알림 개수
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
    	if (userDetails == null) {
    	    log.error("userDetails is null");
    	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    	}
    	Long userNo = userDetails.getUserEntity().getUserNo();
        Long count = notificationService.countUnreadNotifications(userNo);
        return ResponseEntity.ok().body(count);
    }

    // 알림 읽음
    @PatchMapping("/{notificationNo}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable("notificationNo") Long notificationNo) {
        notificationService.markAsRead(notificationNo);
        return ResponseEntity.ok().build();
    }
	
}
