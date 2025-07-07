package com.pettact.api.security.vo;

import java.security.Principal;

public class UserPrincipal implements Principal {
    private final Long userNo;
    private final String email;

    public UserPrincipal(Long userNo, String email) {
        this.userNo = userNo;
        this.email = email;
    }

    public UserPrincipal(Long userNo) {
        this.userNo = userNo;
        this.email = null;
    }

    @Override
    public String getName() {
        return userNo.toString();
    }

    public Long getUserNo() {
        return userNo;
    }

    public String getEmail() {
        return email;
    }
}

