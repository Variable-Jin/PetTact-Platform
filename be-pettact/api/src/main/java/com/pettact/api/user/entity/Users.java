package com.pettact.api.user.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.pettact.api.code.entity.CommonCode;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no")
    private Long userNo;

    @Column(name = "user_email", nullable = false, unique = true)
    private String userEmail;

    @Builder.Default
    @Column(name = "user_email_checked", nullable = false)
    private Boolean userEmailChecked = false;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_nickname", nullable = false)
    private String userNickname;

    @Column(name = "user_password", nullable = false)
    private String userPassword;

    @Column(name = "user_tel", nullable = false)
    private String userTel;

    @Column(name = "user_birth", nullable = false)
    private LocalDate userBirth;

    @Column(name = "user_zipcode")
    private String userZipcode;

    @Column(name = "user_street1")
    private String userStreet1;

    @Column(name = "user_detail_address")
    private String userDetailAddress;

    @Column(name = "user_has_pet", nullable = false)
    private Boolean userHasPet;

    @Column(name = "user_created_at", nullable = false)
    private LocalDateTime userCreatedAt;

    @Column(name = "user_updated_at")
    private LocalDateTime userUpdatedAt;

    @Column(name = "user_deleted_at")
    private LocalDateTime userDeletedAt;

    @Column(name = "user_blacklist", nullable = false)
    private Boolean userBlacklist;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_code", nullable = false)
    private CommonCode roleCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_code", nullable = false)
    private CommonCode statusCode;

}
