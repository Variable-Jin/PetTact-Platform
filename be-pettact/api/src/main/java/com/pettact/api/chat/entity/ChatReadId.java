package com.pettact.api.chat.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatReadId implements Serializable {
 private Long roomNo;
 private Long userNo;
}
