package com.pettact.api.user.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJoinDTO {
	
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$", message = "비밀번호는 영문자, 숫자, 특수문자를 포함해야 합니다.")
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String nickname;

    @NotBlank
    @Pattern(regexp = "^01[016789]\\d{7,8}$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
    private String tel;

    @NotNull
    private LocalDate birth;

    private String zipcode;
    private String street1;
    private String detailAddress;

    @NotNull
    private Boolean hasPet;

    @NotNull
    private Boolean emailChecked;
}
