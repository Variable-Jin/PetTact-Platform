package com.pettact.api.admin.dto.dashboard.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardCountCategoryDTO {
    private String categoryName;
    private Long boardCount;

    public static BoardCountCategoryDTO from(String categoryName, Long count) {
        return BoardCountCategoryDTO.builder()
                .categoryName(categoryName)
                .boardCount(count)
                .build();
    }
}

