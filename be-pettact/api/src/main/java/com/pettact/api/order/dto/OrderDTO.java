package com.pettact.api.order.dto;

import java.util.ArrayList;
import java.util.List;

import com.pettact.api.core.base.BaseEntity;
import com.pettact.api.order.enums.OrderStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderDTO extends BaseEntity {

	private static final long serialVersionUID = -752808634435941379L;
	private Long orderId;
    private Long userId;
    private int totalPrice;
    private OrderStatus status;
    private List<OrderDetailDTO> orderDetailList = new ArrayList<>();
}
