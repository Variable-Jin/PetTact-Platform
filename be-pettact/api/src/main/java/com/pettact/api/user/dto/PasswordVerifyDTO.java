package com.pettact.api.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordVerifyDTO {
	@NotBlank
    private String inputPassword;
}
