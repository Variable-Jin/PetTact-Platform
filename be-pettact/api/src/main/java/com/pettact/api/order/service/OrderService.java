package com.pettact.api.order.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pettact.api.core.base.MapperUtil;
import com.pettact.api.order.dto.OrderDTO;
import com.pettact.api.order.dto.OrderDetailDTO;
import com.pettact.api.order.dto.OrderResponseDTO;
import com.pettact.api.order.entity.OrderDetailEntity;
import com.pettact.api.order.entity.OrderEntity;
import com.pettact.api.order.enums.OrderStatus;
import com.pettact.api.order.repository.OrderDetailRepository;
import com.pettact.api.order.repository.OrderRepository;
import com.pettact.api.product.entity.ProductEntity;
import com.pettact.api.product.repository.ProductRepository;
import com.pettact.api.user.entity.Users;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	
	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final ProductRepository productRepository;
	private final MapperUtil mapperUtil;
	
	// 주문 상세 정보
	private void addOrderDetail(OrderEntity orderEntity, List<OrderDetailDTO> list)  {
		list.stream().forEach(d -> {
			OrderDetailEntity  orderDetailEntity = mapperUtil.map(d, OrderDetailEntity.class);
			ProductEntity productEntity = productRepository.findById(d.getProductNo()).get(); 
			orderDetailEntity.setProduct(productEntity);
			orderDetailEntity.setPrice(productEntity.getProductPrice());
			orderDetailEntity.setOrder(orderEntity);
			orderEntity.addOrderDetail(orderDetailEntity);
			//주문상세를 저장한다 
			orderDetailRepository.save(orderDetailEntity);
		});

		
		//가격을 재계산한다 
		orderEntity.recalcTotalPrice();
		//변경된 가격을 저장한다 
		orderRepository.save(orderEntity);
	}
	
	// 주문 등록
	@Transactional
	public OrderDTO createOrder(Users user, List<OrderDetailDTO> list) { 
		Long orderNo = Long.valueOf(0);
		if (list.size() > 0) {
			orderNo = list.get(0).getOrderNo();
		}
		
		OrderEntity orderEntity = orderNo == null ? null : orderRepository.findById(orderNo).orElse(null);
		if(orderEntity == null) {
		    // 신규 주문 객체 생성
		    orderEntity = OrderEntity.builder()
		            .user(user)
		            .status(OrderStatus.PENDING)
		            .build();
		    // 주문 저장
		   orderRepository.save(orderEntity);
		}

		//기존 주문에 주문상세 항목 추가 
		orderEntity.prePersist();
		addOrderDetail(orderEntity, list);
	    
		return orderEntity.of(mapperUtil);
	}
	
	// 주문 내역 목록
	@Transactional(readOnly = true)
	public List<OrderDTO> getOrdersByUser(Users user) {
		
		 // 사용자 기준 주문 목록 조회
		 return orderRepository.findByUser(user).stream()
				 .map(order -> order.of(mapperUtil)).toList();
	}
	
	// 주문 상세
	@Transactional(readOnly = true)
	public OrderResponseDTO getOrderDetail(Long orderNo, Users user) {
		OrderEntity order = orderRepository.findById(orderNo)
				.orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
		
		if(!order.getUser().getUserNo().equals(user.getUserNo())) {
			throw new SecurityException("해당 주문에 접근할 수 없습니다.");
		}
		OrderResponseDTO dto = mapperUtil.map(order, OrderResponseDTO.class);
		dto.setOrderNo(order.getUser().getUserNo());
		dto.setStatus(order.getStatus().name());
		
	    List<OrderDetailDTO> products = order.getOrderDetailList().stream()
	            .map(detail -> new OrderDetailDTO(
	            		detail.getOrder().getOrderNo(),
	                    detail.getOrderDetailId(),
	                    detail.getProduct().getProductNo(),
	                    detail.getProduct().getProductName(),
	                    detail.getProductStock(),
	                    detail.getPrice()
	            ))
	            .toList();
				
	    dto.setProducts(products);
		return dto;
	}
	
	// 주문 취소
	@Transactional
	public String cancelOrder(Long orderNo, Users user) {
	    OrderEntity order = orderRepository.findById(orderNo)
	            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

	    if (!order.getUser().getUserNo().equals(user.getUserNo())) {
	        throw new SecurityException("해당 주문을 취소할 권한이 없습니다.");
	    }

	    if (!order.getStatus().equals(OrderStatus.PENDING)) {
	        throw new IllegalStateException("진행 중인 주문만 취소할 수 있습니다.");
	    }

	    order.softDelete();
	    order.setStatus(OrderStatus.CANCELLED); // enum에 CANCELLED 상태 추가 필요
	    return "주문 취소가 완료되었습니다.";
	}
}
