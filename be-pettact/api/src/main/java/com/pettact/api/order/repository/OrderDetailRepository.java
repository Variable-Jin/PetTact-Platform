package com.pettact.api.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pettact.api.order.entity.OrderDetailEntity;

// 주문 내역
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {
}