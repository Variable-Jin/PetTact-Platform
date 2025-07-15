package com.pettact.api.notification.enums;

public enum NotificationType {
    APPROVAL("승인 완료"), // 관리자 승인 대기 → 승인 완료
    COMMENT("댓글"),       // 내 게시글에 댓글 달림, 내 댓글에 대댓글 달림
    PET_CHECKUP("정기검진 임박"),
    PET_VACCINE("접종 임박"),
    CHAT_START("채팅 시작"),
    CHAT("채팅"),
    QNA_ANSWER("문의 답변 등록"),
    ADMIN_CHAT("관리자 채팅 알림"),
    REPORT_RESULT("신고 처리 결과"); // 게시물, 판매자 신고 처리 결과

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

