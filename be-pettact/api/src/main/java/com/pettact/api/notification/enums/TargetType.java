package com.pettact.api.notification.enums;

public enum TargetType {
    BOARD("게시글"),
    CHAT_ROOM("채팅방"),
    PRODUCT("상품"),
    PET("반려동물"),
    REPORT("신고"),
    USER("사용자"),
	ADMIN("관리자");
    
    private final String description;
    
    TargetType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
