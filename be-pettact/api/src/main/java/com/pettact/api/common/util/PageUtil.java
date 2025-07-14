package com.pettact.api.common.util;

import java.util.Collections;
import java.util.List;

/**
 * [PageUtil]
 * 리스트 데이터를 페이지네이션 처리하기 위한 공통 유틸 클래스
 */
public class PageUtil {

    /**
     * [getPagedList]
     * 전체 리스트에서 요청 페이지(page)와 페이지 크기(size)에 해당하는 부분 리스트(subList)를 반환한다.
     *
     * @param list 전체 데이터 리스트
     * @param page 현재 페이지 번호 (1부터 시작)
     * @param size 한 페이지에 보여줄 데이터 수
     * @return 요청한 페이지에 해당하는 부분 리스트
     */
    public static <T> List<T> getPagedList(List<T> list, int page, int size) {
        // 리스트가 null이거나 비어있으면 빈 리스트 반환
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        // fromIndex: (page - 1) * size 계산하여 subList 시작 위치 결정
        int fromIndex = (page - 1) * size;

        // toIndex: fromIndex + size, 단 전체 리스트 크기를 넘지 않도록 Math.min 사용
        int toIndex = Math.min(fromIndex + size, list.size());

        // 요청 페이지가 전체 데이터 범위를 벗어나면 빈 리스트 반환
        if (fromIndex >= list.size()) {
            return Collections.emptyList();
        }

        // subList(fromIndex, toIndex) 반환
        return list.subList(fromIndex, toIndex);
    }
}
