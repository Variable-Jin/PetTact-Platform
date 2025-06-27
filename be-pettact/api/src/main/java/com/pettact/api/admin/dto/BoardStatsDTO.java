package com.pettact.api.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardStatsDTO {
    private long totalBoards;
    private long dailyNewBoards;
    private long dailyDeletedBoards;

    public static BoardStatsDTO of(long total, long newToday, long deletedToday) {
        return BoardStatsDTO.builder()
                .totalBoards(total)
                .dailyNewBoards(newToday)
                .dailyDeletedBoards(deletedToday)
                .build();
    }
}

