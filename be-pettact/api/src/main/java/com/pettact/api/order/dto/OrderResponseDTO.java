package com.pettact.api.order.dto;

import java.util.List;

import com.pettact.api.core.base.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO extends BaseEntity{
	//주문 응답 DTO
    private Long orderNo;
    private String status;
    private int totalPrice;
    private List<OrderDetailDTO> products;
    
    // 🟨 배송 정보 추가
    private String deliveryName;
    private String receiver;
    private String zipcode;
    private String address1;
    private String address2;
    private String phone;

}