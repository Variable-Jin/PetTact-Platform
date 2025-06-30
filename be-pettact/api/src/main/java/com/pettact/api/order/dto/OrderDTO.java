package com.pettact.api.order.dto;

import java.util.ArrayList;
import java.util.List;

import com.pettact.api.order.enums.OrderStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderDTO {

	private Long orderNo;
    private Long userNo;
    private int totalPrice;
    private OrderStatus status;
    private List<OrderDetailDTO> orderDetailList = new ArrayList<>();
}
