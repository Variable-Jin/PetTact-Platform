package com.pettact.api.cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pettact.api.cart.entity.CartEntity;
import com.pettact.api.product.entity.ProductEntity;
import com.pettact.api.user.entity.Users;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
	
    // 해당 유저의 모든 장바구니 목록 조회
	List<CartEntity> findByUser_UserNo(Long userNo);


    // 유저와 상품으로 장바구니 아이템 존재 여부 확인
    Optional<CartEntity> findByUserAndProduct(Users userNo, ProductEntity product);

    // 유저의 장바구니에서 특정 상품 제거
    void deleteByUserAndProduct(Users user, ProductEntity product);
   
}
