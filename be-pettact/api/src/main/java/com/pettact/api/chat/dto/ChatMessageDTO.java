package com.pettact.api.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder 
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {
	
	private Long messageId; 

    private Long roomNo;

    private Long senderUserNo;
    private String senderNickname;

    private String message;
}
