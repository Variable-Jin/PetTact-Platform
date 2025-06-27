package com.pettact.api.admin.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/* 통계 쪽 날짜 계산용*/
public class DateRangeUtil {
	// 하루
    public static LocalDateTime startOfToday() {
        return LocalDate.now().atStartOfDay();
    }

    public static LocalDateTime endOfToday() {
        return LocalDate.now().atTime(LocalTime.MAX);
    }
    
	// 어제
    public static LocalDateTime startOfYesterday() {
    	return LocalDate.now().minusDays(1).atStartOfDay();
    }
    
    public static LocalDateTime endOfYesterday() {
    	return LocalDate.now().minusDays(1).atTime(LocalTime.MAX);
    }
		
    // 이번 주 (오늘 포함, 7일)
    public static LocalDateTime startOfLast7Days() {
        return LocalDate.now().minusDays(6).atStartOfDay(); // 오늘 포함 7일
    }

    public static LocalDateTime endOfLast7Days() {
        return LocalDate.now().atTime(LocalTime.MAX);
    }

    // 이번 달 (1일부터 오늘까지)
    public static LocalDateTime startOfThisMonth() {
        return LocalDate.now().withDayOfMonth(1).atStartOfDay();
    }

    public static LocalDateTime endOfThisMonthUntilToday() {
        return LocalDate.now().atTime(LocalTime.MAX);
    }

    // 특정 날짜 범위
    public static LocalDateTime startOf(LocalDate date) {
        return date.atStartOfDay();
    }

    public static LocalDateTime endOf(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }
	
}
