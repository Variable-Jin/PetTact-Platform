package com.pettact.api.cart.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pettact.api.cart.dto.CartDTO;
import com.pettact.api.cart.entity.CartEntity;
import com.pettact.api.cart.repository.CartRepository;
import com.pettact.api.product.entity.ProductEntity;
import com.pettact.api.product.repository.ProductRepository;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {
	
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository; 
    
    
    // 장바구니 상품 목록 조회
    public List<CartDTO> getCartProduct(Long userNo) {
        
    	List<CartEntity> cartList = cartRepository.findByUser_UserNo(userNo);
    	 System.out.println(">>> 장바구니 조회된 개수: " + cartList.size());
    	 
    	 
        return cartRepository.findByUser_UserNo(userNo)
                .stream()
                .filter(cart -> cart.getProduct() != null && !cart.getProduct().isDeleted())
                .map(cart -> {
                	ProductEntity product = cart.getProduct();
                    // 여기서 imageUrl 로그 찍기
                    System.out.println(">>> 상품 정보: productNo=" + product.getProductNo()
                        + ", productName=" + product.getProductName()
                        + ", imageUrl=" + product.getImageUrl());
                    
                    return CartDTO.builder()
                    		.cartNo(cart.getCartNo())
                            .productNo(cart.getProduct().getProductNo())
                            .productName(cart.getProduct().getProductName())
                            .productPrice(cart.getProduct().getProductPrice())
                            .productStock(cart.getProductStock())
                            .totalPrice(cart.getProduct().getProductPrice() * cart.getProductStock())
                            .imageUrl(cart.getProduct().getImageUrl())
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    // 장바구니 상품 삭제
    public void deleteProduct(Users user, Long cartNo) {
    	
    	CartEntity cart = cartRepository.findById(cartNo)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // 혹시 해당 cart가 현재 로그인한 사용자(user)에 속하는지 확인 (보안상)
        if (!cart.getUser().getUserNo().equals(user.getUserNo())) {
            throw new IllegalArgumentException("해당 장바구니 상품에 접근 권한이 없습니다.");
        }
        cart.softDelete();
        cartRepository.delete(cart);
    }
    
    // 장바구니 수량 업데이트
    @Transactional
    public void updateQuantity(Users user ,Long productNo, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        
        ProductEntity product = productRepository.findById(productNo)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        CartEntity cart = cartRepository.findByUserAndProduct(user, product)
            .orElseThrow(() -> new IllegalArgumentException("장바구니에 상품이 없습니다."));
        
        cart.setProductStock(quantity);
        cart.prePersist();
        cartRepository.save(cart);
    }
    
    // 장바구니 상품 추가 ( 동일 상품 등록 시에 수량 증가 )
    public void addToCart(Users user, Long productNo, int quantity) {
    	
        // 수량 검증
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        
        ProductEntity product = productRepository.findById(productNo)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        CartEntity cart = cartRepository.findByUserAndProduct(user, product)
                .orElse(CartEntity.builder()
                        .user(user)
                        .product(product)
                        .productStock(product.getProductStock())
                        .productPrice(product.getProductPrice())
                        .build());

        cart.setProductStock(cart.getProductStock() + quantity);
        cart.prePersist();
        cartRepository.save(cart);
    }
}
