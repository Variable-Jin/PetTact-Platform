package com.pettact.api.chat.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatUserDTO {
 private String nickname;

 public ChatUserDTO(String nickname) {
     this.nickname = nickname;
 }

 public String getNickname() {
     return nickname;
 }

 public void setNickname(String nickname) {
     this.nickname = nickname;
 }
}

