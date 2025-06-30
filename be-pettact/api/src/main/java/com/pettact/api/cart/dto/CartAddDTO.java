package com.pettact.api.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartAddDTO {
	// 장바구니 상품 등록 DTO
	private Long cartNo;
	@NotNull
    private Long productNo;
	@Min(1)
    private int productStock;
}
