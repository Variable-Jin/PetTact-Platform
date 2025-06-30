package com.pettact.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPatchDTO {
	
    private String userNickname;
    private String userTel;
    private String userZipcode;
    private String userStreet1;
    private String userDetailAddress;
}
