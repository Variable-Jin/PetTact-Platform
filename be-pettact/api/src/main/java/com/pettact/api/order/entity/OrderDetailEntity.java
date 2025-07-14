package com.pettact.api.order.entity;

import com.pettact.api.core.base.BaseEntity;
import com.pettact.api.core.base.MapperUtil;
import com.pettact.api.order.dto.OrderDetailDTO;
import com.pettact.api.order.enums.OrderStatus;
import com.pettact.api.product.entity.ProductEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "order_detail") // 주문 내역
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailEntity extends BaseEntity {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderDetailId; // 주문 내역 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_no")
    private OrderEntity order; // 주문 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_no")
    private ProductEntity product; // 상품 번호
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private int productStock; // 수량

    private int productPrice; // 단가
    
    public int getTotalPrice() {
    	return productStock * productPrice; // 실제 결제금액
    }
    
    public OrderDetailDTO of(MapperUtil mapperUtil) {
    	OrderDetailDTO orderDetailDTO = mapperUtil.map(this, OrderDetailDTO.class);
    	orderDetailDTO.setProductNo(product.getProductNo());
    	orderDetailDTO.setProductName(product.getProductName());
    	orderDetailDTO.setProductPrice(product.getProductPrice());
    	orderDetailDTO.setProductStock(this.productStock);
    	orderDetailDTO.setImageUrl(product.getImageUrl());  // 여기 추가
    	return orderDetailDTO;
    }
}
