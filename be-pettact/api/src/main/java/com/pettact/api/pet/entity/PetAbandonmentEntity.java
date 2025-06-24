package com.pettact.api.pet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pet_abandonment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetAbandonmentEntity {

    @Id
    private String desertionNo;

    private String noticeNo;
    private String happenDt;     // yyyyMMdd
    private String happenPlace;

    private String kindFullNm;
    private String kindCd;
    private String kindNm;
    private String upKindCd;
    private String upKindNm;

    private String colorCd;
    private String age;
    private String weight;

    private String noticeSdt;    // yyyyMMdd
    private String noticeEdt;    // yyyyMMdd

    private String processState;
    private String sexCd;
    private String neuterYn;

    @Column(columnDefinition = "TEXT")
    private String specialMark;

    private String careRegNo;
    private String careNm;
    private String careTel;

    @Column(columnDefinition = "TEXT")
    private String careAddr;

    private String orgNm;
    private String careOwnerNm;

    private String popfile1;
    private String popfile2;
    private String popfile3;
    private String popfile4;
    private String popfile5;
    private String popfile6;
    private String popfile7;
    private String popfile8;

    private String rfidCd;
    private String evntImg;
    private String endReason;
    private String etcBigo;

    @Column(columnDefinition = "TEXT")
    private String sfeSoci;

    @Column(columnDefinition = "TEXT")
    private String sfeHealth;

    private String vaccinationChk;
    private String healthChk;

    private String adptnTitle;
    private String adptnSDate;
    private String adptnEDate;

    @Column(columnDefinition = "TEXT")
    private String adptnConditionLimitTxt;

    @Column(columnDefinition = "TEXT")
    private String adptnTxt;

    private String adptnImg;

    private String sprtTitle;
    private String sprtSDate;
    private String sprtEDate;

    @Column(columnDefinition = "TEXT")
    private String sprtConditionLimitTxt;

    @Column(columnDefinition = "TEXT")
    private String sprtTxt;

    private String sprtImg;

    private String srvcTitle;
    private String srvcSDate;
    private String srvcEDate;

    @Column(columnDefinition = "TEXT")
    private String srvcConditionLimitTxt;

    @Column(columnDefinition = "TEXT")
    private String srvcTxt;

    private String srvcImg;

    private String evntTitle;
    private String evntSDate;
    private String evntEDate;

    @Column(columnDefinition = "TEXT")
    private String evntConditionLimitTxt;

    @Column(columnDefinition = "TEXT")
    private String evntTxt;

    private LocalDateTime updTm;  // yyyy-MM-dd HH:mm:ss.S
}
