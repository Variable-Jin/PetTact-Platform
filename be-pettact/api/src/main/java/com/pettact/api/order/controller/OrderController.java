package com.pettact.api.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.order.dto.OrderDTO;
import com.pettact.api.order.dto.OrderDetailDTO;
import com.pettact.api.order.dto.OrderResponseDTO;
import com.pettact.api.order.service.OrderService;
import com.pettact.api.security.vo.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    
    // 주문 등록
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@AuthenticationPrincipal CustomUserDetails user, @RequestBody List<OrderDetailDTO> list) {
        return ResponseEntity.ok(orderService.createOrder(user.getUserEntity(), list));
    }
    
    // 주문 내역 목록 
    @GetMapping("/list")
    public ResponseEntity<List<OrderDTO>> getUserOrders(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(orderService.getOrdersByUser(user.getUserEntity()));
    }
    
    // 주문 상세
    @GetMapping("/detail/{orderNo}")
    public ResponseEntity<OrderResponseDTO> getOrderDetail(@PathVariable("orderNo") Long orderNo, @AuthenticationPrincipal CustomUserDetails user){
    	
    	OrderResponseDTO dto = orderService.getOrderDetail(orderNo, user.getUserEntity());
    	return ResponseEntity.ok(dto);
    }
    
    // 주문 취소
    @PatchMapping("/cancel/{orderNo}")
    public ResponseEntity<String> cancelOrder(@PathVariable("orderNo") Long orderNo,
                                            @AuthenticationPrincipal CustomUserDetails user) {
        String message = orderService.cancelOrder(orderNo, user.getUserEntity());
        return ResponseEntity.ok(message);
    }
}