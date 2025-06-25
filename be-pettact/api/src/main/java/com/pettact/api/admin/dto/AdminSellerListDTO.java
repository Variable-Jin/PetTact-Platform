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
public class AdminSellerListDTO {
    private Long userNo;
    private String userEmail;
    private String userNickname;
    private String userName;
    private String userTel;
    private String roleCode;
    private String statusCode;
    private LocalDateTime createdAt;
    private Boolean isDeleted;

    public static AdminSellerListDTO from(Users user) {
        return AdminSellerListDTO.builder()
                .userNo(user.getUserNo())
                .userEmail(user.getUserEmail())
                .userNickname(user.getUserNickname())
                .userName(user.getUserName())
                .userTel(user.getUserTel())
                .roleCode(user.getRoleCode())
                .statusCode(user.getStatusCode())
                .createdAt(user.getCreatedAt())
                .isDeleted(user.isDeleted())
                .build();
    }
}
