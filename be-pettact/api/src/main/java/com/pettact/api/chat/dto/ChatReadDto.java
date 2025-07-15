package com.pettact.api.chat.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatReadDto {
    private Long roomNo;
    private Long lastMessageId;
}
