package com.pettact.api.admin.dto.dashboard.report;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDailyStatsDTO {
    private LocalDate date;
    private Long reportCount;

    public static ReportDailyStatsDTO of(LocalDate date, Long count) {
        return ReportDailyStatsDTO.builder()
                .date(date)
                .reportCount(count)
                .build();
    }
}
