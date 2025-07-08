package com.pettact.api.chat.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRoomDTO {
	private Long roomNo;
	private String name;
	
	public ChatRoomDTO(Long roomNo, String name) {
		this.roomNo = roomNo;
		this.name = name;
	}
}
