package com.pettact.api.product.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailDTO {
	
	//상품 상세보기 DTO
	
	private Long productsNo; 			  // 상품 번호
    private String productsName;          // 상품 이름
    private String productsDescription;   // 상품 설명
    private Integer productsPrice;        // 상품 가격
    private Integer productsStock;     // 상품 수량
    private String categoryName;      // 상품 카테고리 이름
    private LocalDateTime createdAt; // 상품 등록 시간
    private boolean status;       // true: 판매중, false: 품절

    // 작성자 정보
    private Long userNo;       // 작성자 ID
    private String userName;   // 작성자 이름
    

}
