package com.pettact.api.product.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "products_no")
    private Long id; // 상품 번호

    @Column(name = "products_name", nullable = false, length = 100)
    private String name; // 상품 이름

    @Column(name = "products_description", columnDefinition = "TEXT")
    private String description; // 상품 설명

    @Column(name = "products_price", nullable = false)
    private Integer price; // 상품 가격

    @Column(name = "products_stock", nullable = false)
    private Integer quantity; // 상품 수량

    @Column(name = "products_created_at", nullable = false)
    private LocalDateTime createdAt; // 상품 등록시간

    @Column(name = "products_category", length = 50)
    private String category; // 상품 카테고리
    
    @Column(name = "products_status", nullable = false)
    private boolean status; // true: 판매중, false: 비활성/품절
    
}

