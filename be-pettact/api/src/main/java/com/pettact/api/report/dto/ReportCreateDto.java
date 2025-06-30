package com.pettact.api.report.dto;


import com.pettact.api.report.entity.Report;
import com.pettact.api.user.entity.Users;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReportCreateDto {

    private Long userNo;
    private Report.ReportTargetLocation reportTargetLocation;
    private Long reportTargetNo;
    private String reportReason;
    private String reportDescription;


    public Report toEntity(Users users) {
        return new Report(
                users,
                reportTargetLocation,
                reportTargetNo,
                reportReason,
                reportDescription,
                0
        );
    }

}
