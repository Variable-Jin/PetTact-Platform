package com.pettact.api.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pettact.api.product.entity.ProductEntity;
import com.pettact.api.user.entity.Users;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>{
	
	List<ProductEntity> findByUser(Users user);
	
	Optional<ProductEntity> findByProductNo(Long productNo);
	
	// ✅ 정렬 가능한 상품 목록 조회용 (getAllProduct에서만 사용)
	Page<ProductEntity> findByProductNameContainingIgnoreCaseAndProductCategory_CategoryNoAndIsDeletedFalse(String keyword, Long categoryNo, Pageable pageable);

	Page<ProductEntity> findByProductNameContainingIgnoreCaseAndIsDeletedFalse(String keyword, Pageable pageable);

	Page<ProductEntity> findByProductCategory_CategoryNoAndIsDeletedFalse(Long categoryNo, Pageable pageable);

	Page<ProductEntity> findByIsDeletedFalse(Pageable pageable);

    // 페이징으로 전체 상품 조회, 최신순 정렬 기본
    //Page<ProductEntity> findAllByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
    
}
