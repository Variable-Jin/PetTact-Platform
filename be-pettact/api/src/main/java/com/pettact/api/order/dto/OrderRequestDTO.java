package com.pettact.api.order.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
	// 주문 요청 DTO
    private Long orderNo;
    private String createdAt;
    private String status;
    private int totalPrice;
    private List<OrderDetailDTO> products;
    
}
