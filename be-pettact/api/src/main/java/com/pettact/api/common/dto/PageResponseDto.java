package com.pettact.api.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResponseDto<T> {
    private List<T> content;
    private int totalElements;
    private int currentPage;
    private int pageSize;
}
