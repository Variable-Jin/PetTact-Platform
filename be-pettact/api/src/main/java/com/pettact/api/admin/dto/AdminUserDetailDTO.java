package com.pettact.api.admin.dto;

import java.time.LocalDate;
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
public class AdminUserDetailDTO {
    private Long userNo;
    private String userEmail;
    private String userNickname;
    private String userName;
    private String userTel;
    private LocalDate userBirth;
    private String userZipcode;
    private String userStreet1;
    private String userDetailAddress;
    private Boolean userHasPet;
    private Boolean userEmailChecked;
    private Boolean userBlacklist;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;
    private String roleCode;
    private String statusCode;
    private String provider;
    
    public static AdminUserDetailDTO from(Users user) {
        return AdminUserDetailDTO.builder()
                .userNo(user.getUserNo())
                .userEmail(user.getUserEmail())
                .userName(user.getUserName())
                .userNickname(user.getUserNickname())
                .userTel(user.getUserTel())
                .userBirth(user.getUserBirth())
                .userZipcode(user.getUserZipcode())
                .userStreet1(user.getUserStreet1())
                .userDetailAddress(user.getUserDetailAddress())
                .userHasPet(user.getUserHasPet())
                .userEmailChecked(user.getUserEmailChecked())
                .userBlacklist(user.getUserBlacklist())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .isDeleted(user.isDeleted())
                .deletedAt(user.getDeletedAt())
                .roleCode(user.getRoleCode())
                .statusCode(user.getStatusCode())
                .provider(user.getProvider())
                .build();
    }

}
