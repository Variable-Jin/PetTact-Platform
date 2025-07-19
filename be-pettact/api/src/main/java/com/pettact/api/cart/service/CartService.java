package com.pettact.api.cart.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Page<CartDTO> getCartProduct(Long userNo, int page, int size) {
    	
        // 최근 등록된 상품이 먼저 오도록 정렬
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    	
    	//Pageable pageable = PageRequest.of(page, size);
        
    	Page<CartEntity> cartPage = cartRepository.findByUser_UserNo(userNo ,pageable);

    	 //System.out.println(">>> 장바구니 조회된 개수: " + cartPage.size());
    	 
    	 
    	    List<CartDTO> cartDTOList = cartPage.getContent().stream()
    	            .filter(cart -> cart.getProduct() != null && !cart.getProduct().isDeleted())
    	            .map(cart -> {
    	                ProductEntity product = cart.getProduct();
    	                return CartDTO.builder()
    	                    .cartNo(cart.getCartNo())
    	                    .productNo(product.getProductNo())
    	                    .productName(product.getProductName())
    	                    .productPrice(product.getProductPrice())
    	                    .productStock(cart.getProductStock())
    	                    .totalPrice(product.getProductPrice() * cart.getProductStock())
    	                    .imageUrl(product.getImageUrl())
    	                    .build();
    	            }).toList();

    	        return new PageImpl<>(cartDTOList, pageable, cartPage.getTotalElements());
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
                        .productStock(0)
                        .productPrice(product.getProductPrice())
                        .build());

        cart.setProductStock(cart.getProductStock() + quantity);
        cart.prePersist();
        cartRepository.save(cart);
    }
}
