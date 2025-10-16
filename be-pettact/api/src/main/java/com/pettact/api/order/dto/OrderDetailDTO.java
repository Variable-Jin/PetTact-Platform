package com.pettact.api.order.dto;

import lombok.Data;

@Data
public class OrderDetailDTO {
	
    private Long orderNo;
	private Long orderDetailNo;
	private Long productNo;
	private String productName;
    private int productStock;
    private int productPrice;
    private String imageUrl;  // 추가
    
    // 기본 생성자 추가 (Jackson 역직렬화에 필요)
    public OrderDetailDTO() {}
    
    public OrderDetailDTO(Long orderNo, Long orderDetailNo, Long productNo, String productName, int productStock, int productPrice, String imageUrl) {
        this.orderNo = orderNo;
        this.orderDetailNo = orderDetailNo;
        this.productNo = productNo;
        this.productName = productName;
        this.productStock = productStock;
        this.productPrice = productPrice;
        this.imageUrl = imageUrl;
    }

}
