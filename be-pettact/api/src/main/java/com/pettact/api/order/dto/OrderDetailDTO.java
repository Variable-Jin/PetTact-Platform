package com.pettact.api.order.dto;

import lombok.Data;

@Data
public class OrderDetailDTO {
	
    private Long orderId;
	private Long orderDetailId;
	private Long productNo;
    private String name;
    private int quantity;
    private int price;
    
    // 기본 생성자 추가 (Jackson 역직렬화에 필요)
    public OrderDetailDTO() {}
    
    public OrderDetailDTO(Long orderId, Long orderDetailId, Long productNo, String name, int quantity, int price) {
        this.orderId = orderId;
        this.orderDetailId = orderDetailId;
        this.productNo = productNo;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

}
