package com.pettact.api.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalUsers;			// 전체 회원
    private Long activeUsers;			// 활성 상태인 회원
    private long totalSellers;			// 판매자 수
    private long pendingSellers;		// 판매자 권한 승인 대기중인 회원
    
    private Long dailyNewUsers;			// 오늘 신규가입자
    private Long weeklyNewUsers;		// 주별 신규가입자
    private Long monthlyNewUsers;		// 월별 신규가입자
    private Double userGrowthRate;		// 전일 대비 증가율
    
    public static DashboardStatsDTO of(long totalUsers, long activeUsers, long totalSellers, long pendingSellers,
            long today, long weekly, long monthly, long yesterday) {

	double growthRate = (yesterday == 0) ? 100.0 : ((double) (today - yesterday) / yesterday) * 100;
	
	return DashboardStatsDTO.builder()
		.totalUsers(totalUsers)
		.activeUsers(activeUsers)
		.totalSellers(totalSellers)
		.pendingSellers(pendingSellers)
		.dailyNewUsers(today)
		.weeklyNewUsers(weekly)
		.monthlyNewUsers(monthly)
		.userGrowthRate(growthRate)
		.build();
	}
}
