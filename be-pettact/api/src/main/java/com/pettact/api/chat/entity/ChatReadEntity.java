package com.pettact.api.chat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor  
@AllArgsConstructor
@Table(name = "chat_read")
@IdClass(ChatReadId.class)
public class ChatReadEntity {

    @Id
    private Long roomNo;

    @Id
    private Long userNo;

    private Long lastReadMessageNo;
}
