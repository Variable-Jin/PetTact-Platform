package com.pettact.api.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
	private Long roomNo;
	private String name;
	private Long user1No;
	private String user1Nickname;
	private Long user2No;
	private String user2Nickname;
	
	//방조회
	public ChatRoomDTO(Long roomNo, String name) {
	    this.roomNo = roomNo;
	    this.name = name;
	}
}
