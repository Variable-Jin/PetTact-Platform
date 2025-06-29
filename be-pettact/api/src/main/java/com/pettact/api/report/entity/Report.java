package com.pettact.api.report.entity;


import com.pettact.api.core.base.BaseEntity;
import com.pettact.api.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REPORT_NO")
    private Long reportNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_NO")
    private Users users;

    // Enum
    @Column(name = "REPORT_TARGET_LOCATION")
    private ReportTargetLocation reportTargetLocation;

    @Column(name = "REPORT_TARGET_NO")
    private Long reportTargetNo;

    @Column(name = "REPORT_REASON")
    private String reportReason;

    @Column(name = "REPORT_DESCRIPTION")
    private String reportDescription;

    @Column(name = "REPORT_STATUS")
    private Integer reportStatus;
    private static final int PENDING = 0;
    private static final int APPROVED = 1;
    private static final int REJECTED = 2;

    // @Column(name = "RPORT_IP")
    // private String reportIP;



    public Report(Users users, ReportTargetLocation reportTargetLocation, Long reportTargetNo, String reportReason, String reportDescription, Integer reportStatus) {
        super();
        this.users = users;
        this.reportTargetLocation = reportTargetLocation;
        this.reportTargetNo = reportTargetNo;
        this.reportReason = reportReason;
        this.reportDescription = reportDescription;
        this.reportStatus = reportStatus;
    }


    public enum ReportTargetLocation {
        BOARD,
        REPLY,
        PRODUCT,
        CART,
        ORDER,
        PET,
        USER;
    }

}
