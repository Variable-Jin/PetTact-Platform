package com.pettact.api.order.entity;

import com.pettact.api.core.base.BaseEntity;
import com.pettact.api.core.base.MapperUtil;
import com.pettact.api.order.dto.OrderDetailDTO;
import com.pettact.api.product.entity.ProductEntity;

import jakarta.persistence.Entity;
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

    private int productStock; // 수량

    private int price; // 단가
    
    public int getTotalPrice() {
    	return productStock * price;
    }
    
    public OrderDetailDTO of(MapperUtil mapperUtil) {
    	OrderDetailDTO orderDetailDTO = mapperUtil.map(this, OrderDetailDTO.class);
    	orderDetailDTO.setProductNo(product.getProductNo());
    	orderDetailDTO.setName(product.getProductName());
    	return orderDetailDTO;
    }
}
