package com.pettact.api.admin.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyUserStatsDTO {
    private LocalDate date;
    private Long newUsers;
    private Long totalUsers;

    // 다른 타입의 객체를 입력받는게 아니라면 of를 사용
    public static DailyUserStatsDTO of(LocalDate date, long newUsers, long totalUsers) {
        return DailyUserStatsDTO.builder()
            .date(date)
            .newUsers(newUsers)
            .totalUsers(totalUsers)
            .build();
    }
}

