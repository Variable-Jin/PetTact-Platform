package com.pettact.api.user.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocialJoinDTO {
    @NotBlank(message = "이름을 입력해주세요.")
    private String userName;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String userNickname;

    @NotBlank(message = "휴대폰 번호를 입력해주세요.")
    @Pattern(regexp = "^01[016789]\\d{7,8}$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
    private String userTel;

    @NotNull(message = "생년월일을 입력해주세요.")
    private LocalDate userBirth;

    private String userZipcode;
    private String userStreet1;
    private String userDetailAddress;
    
    @NotNull
    private Boolean userHasPet;
    
    @NotNull
    private Boolean userEmailChecked;
}

