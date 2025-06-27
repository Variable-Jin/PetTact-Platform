package com.pettact.api.admin.dto;

import java.time.LocalDateTime;

import com.pettact.api.user.entity.Users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserListDTO {
    private Long userNo;
    private String userEmail;
    private String userNickname;
    private String userName;
    private String userTel;
    private Boolean userHasPet;
    private Boolean userEmailChecked;
    private Boolean userBlacklist;
    private LocalDateTime createdAt;
    private Boolean isDeleted;
    private String roleCode;
    private String statusCode;
    private String provider;
    
    public static AdminUserListDTO from(Users user) {
        return AdminUserListDTO.builder()
                .userNo(user.getUserNo())
                .userEmail(user.getUserEmail())
                .userName(user.getUserName())
                .userNickname(user.getUserNickname())
                .userTel(user.getUserTel())
                .userHasPet(user.getUserHasPet())
                .userEmailChecked(user.getUserEmailChecked())
                .userBlacklist(user.getUserBlacklist())
                .createdAt(user.getCreatedAt())
                .isDeleted(user.isDeleted())
                .roleCode(user.getRoleCode())
                .statusCode(user.getStatusCode())
                .provider(user.getProvider())
                .build();
    }
}
