package com.pettact.api.chat.dto;

import lombok.Data;

@Data
public class ChatMessageDTO {
	
	public enum MessageType {
        ENTER, TALK, LEAVE
    }

    private MessageType type;
    private Long roomNo;
    private String sender;
    private String message;

}
