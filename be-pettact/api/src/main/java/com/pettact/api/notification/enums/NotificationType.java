package com.pettact.api.notification.enums;

public enum NotificationType {
    COMMENT("댓글"),
    CHAT("채팅"),
    APPROVAL("승인"),
    REMINDER("알림"),
    REPORT_RESULT("신고 처리"),
    PURCHASE_REQUEST("구매 요청");
    
    private final String description;
    
    NotificationType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
