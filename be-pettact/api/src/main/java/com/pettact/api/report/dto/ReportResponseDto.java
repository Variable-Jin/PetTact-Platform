package com.pettact.api.report.dto;

import com.pettact.api.report.entity.Report;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReportResponseDto {

    private Long reportNo;
    private Long userNo;
    private Report.ReportTargetLocation reportTargetLocation;
    private Long reportTargetNo;
    private String reportReason;
    private String reportDescription;
    private Integer reportStatus;
    private LocalDateTime createdAt;    // 신고 접수 시간
    private LocalDateTime updatedAt;    // 신고 처리 시간
    private String reportIP;

    public static ReportResponseDto fromEntity(Report savedReport) {
        return new ReportResponseDto(
                savedReport.getReportNo(),
                savedReport.getUsers().getUserNo(),
                savedReport.getReportTargetLocation(),
                savedReport.getReportTargetNo(),
                savedReport.getReportReason(),
                savedReport.getReportDescription(),
                savedReport.getReportStatus(),
                savedReport.getCreatedAt(),
                savedReport.getUpdatedAt(),
                savedReport.getReportIP()
        );
    }
}
