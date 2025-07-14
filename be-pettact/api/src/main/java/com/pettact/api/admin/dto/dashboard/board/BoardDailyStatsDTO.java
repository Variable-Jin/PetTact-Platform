package com.pettact.api.admin.dto.dashboard.board;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDailyStatsDTO {
    private LocalDate date;
    private Long newBoards;

    public static BoardDailyStatsDTO of(LocalDate date, Long count) {
        return BoardDailyStatsDTO.builder()
                .date(date)
                .newBoards(count)
                .build();
    }
}

