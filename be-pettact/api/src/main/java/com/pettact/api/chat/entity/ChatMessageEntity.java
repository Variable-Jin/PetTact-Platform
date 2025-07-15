package com.pettact.api.chat.entity;

import com.pettact.api.core.base.BaseEntity;
import com.pettact.api.user.entity.Users;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_message")
@Data
@Builder
@NoArgsConstructor  
@AllArgsConstructor
public class ChatMessageEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatMessageNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_no", nullable = false)
    private ChatRoomEntity chatRoom;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender", nullable = false)
    private Users sender;
    
    private String message;
    
    

}
