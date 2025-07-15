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
    @Enumerated(EnumType.STRING)
    @Column(name = "REPORT_TARGET_LOCATION")
    private ReportTargetLocation reportTargetLocation;

    @Column(name = "REPORT_TARGET_NO")
    private Long reportTargetNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "REPORT_REASON")
    private ReportReason reportReason;

    @Column(name = "REPORT_DESCRIPTION")
    private String reportDescription;

    @Column(name = "REPORT_STATUS")
    private Integer reportStatus;
    private static final int PENDING = 0;
    private static final int APPROVED = 1;
    private static final int REJECTED = 2;

     @Column(name = "RPORT_IP")
     private String reportIP;



    public Report(Users users, ReportTargetLocation reportTargetLocation, Long reportTargetNo, ReportReason reportReason, String reportDescription, Integer reportStatus) {
        super();
        this.users = users;
        this.reportTargetLocation = reportTargetLocation;
        this.reportTargetNo = reportTargetNo;
        this.reportReason = reportReason;
        this.reportDescription = reportDescription;
        this.reportStatus = reportStatus;
    }

    public enum ReportReason {
        SPAM("스팸홍보/도배입니다"),
        INAPPROPRIATE("음란물입니다"),
        FALSE_INFO("불법정보를 포함하고 있습니다"),
        HARMFUL_TO_MINORS("청소년에게 유해한 내용입니다"),
        PRIVACY_VIOLATION("욕설/생명경시/혐오/차별적 표현입니다"),
        PERSONAL_INFO("개인정보가 노출되었습니다"),
        ILLEGAL("불쾌한 표현이 있습니다");

        private final String description;

        ReportReason(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
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
