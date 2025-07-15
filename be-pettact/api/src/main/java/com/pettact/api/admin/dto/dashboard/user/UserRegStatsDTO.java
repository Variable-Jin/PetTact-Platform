package com.pettact.api.admin.dto.dashboard.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegStatsDTO {
    private Long todayCount;
    private Long yesterdayCount;
    private Long weeklyCount;
    private Long monthlyCount;

    public static UserRegStatsDTO of(long today, long yesterday, long weekly, long monthly) {
        return UserRegStatsDTO.builder()
                .todayCount(today)
                .yesterdayCount(yesterday)
                .weeklyCount(weekly)
                .monthlyCount(monthly)
                .build();
    }
}

