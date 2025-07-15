package com.pettact.api.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
	
	private Long cartNo;		  // 장바구니 번호
    private Long productNo;       // 상품 번호
    private String productName;   // 상품 이름
    private Integer productPrice; // 상품 가격
    private Integer productStock;     // 수량
    private Integer totalPrice;   // 총 가격 (상품 가격 * 수량)
    private String imageUrl;  // 추가
}
