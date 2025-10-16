package com.pettact.api.cart.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.cart.dto.CartAddDTO;
import com.pettact.api.cart.dto.CartDTO;
import com.pettact.api.cart.dto.CartUpdateDTO;
import com.pettact.api.cart.service.CartService;
import com.pettact.api.security.vo.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
	
    private final CartService cartService;
    
    // 장바구니 상품 목록 
    @GetMapping
    public ResponseEntity<Page<CartDTO>> getCartProduct(@AuthenticationPrincipal CustomUserDetails user,
    		@RequestParam(name = "page",defaultValue = "0") int page,
    		@RequestParam(name = "size",defaultValue = "10") int size) {

    	return ResponseEntity.ok(cartService.getCartProduct(user.getUserEntity().getUserNo(), page, size));
    }
    
    // 장바구니 상품 삭제
    @DeleteMapping("/delete/{cartNo}")
    public ResponseEntity<String> deleteProduct(@AuthenticationPrincipal CustomUserDetails user, @PathVariable("cartNo") Long cartNo) {
        cartService.deleteProduct(user.getUserEntity(),cartNo);
        return ResponseEntity.ok("장바구니에서 상품이 삭제되었습니다.");
    }
    
    // 장바구니 수량 변경
    @PatchMapping("/{productNo}")
    public ResponseEntity<String> updateQuantity(@AuthenticationPrincipal CustomUserDetails user,@RequestBody @Valid CartUpdateDTO dto) {
        cartService.updateQuantity(user.getUserEntity(), dto.getProductNo(), dto.getProductStock());
        return ResponseEntity.ok("장바구니 수량이 변경되었습니다.");
    }
    
    //장바구니 상품 추가 ( 동일 상품 등록 시에 수량 증가 )
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@AuthenticationPrincipal CustomUserDetails user, @RequestBody CartAddDTO dto) {
        cartService.addToCart(user.getUserEntity(), dto.getProductNo(), dto.getProductStock());
        return ResponseEntity.ok("장바구니에 추가되었습니다.");
    }

}
