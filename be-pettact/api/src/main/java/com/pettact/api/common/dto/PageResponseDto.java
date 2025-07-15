package com.pettact.api.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor
public class PageResponseDto<T> {
    private List<T> content;
    private int totalElements;
    private int currentPage;
    private int pageSize;
    
    public static <T> PageResponseDto<T> from(Page<T> page) {
        return new PageResponseDto<>(
            page.getContent(),
            (int) page.getTotalElements(),
            page.getNumber() + 1, // 프론트에서 1페이지부터 보이게
            page.getSize()
        );
    }
}
