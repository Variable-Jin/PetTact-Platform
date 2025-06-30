package com.pettact.api.order.dto;

import lombok.Data;

@Data
public class OrderDetailDTO {
	
    private Long orderNo;
	private Long orderDetailId;
	private Long productNo;
    private String name;
    private int productStock;
    private int price;
    
    // 기본 생성자 추가 (Jackson 역직렬화에 필요)
    public OrderDetailDTO() {}
    
    public OrderDetailDTO(Long orderNo, Long orderDetailId, Long productNo, String name, int productStock, int price) {
        this.orderNo = orderNo;
        this.orderDetailId = orderDetailId;
        this.productNo = productNo;
        this.name = name;
        this.productStock = productStock;
        this.price = price;
    }

}
