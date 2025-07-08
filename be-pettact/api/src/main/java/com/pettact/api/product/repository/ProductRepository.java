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
	
	List<ProductEntity> findAllByOrderByCreatedAtDesc();
	
	List<ProductEntity> findByProductNameContainingIgnoreCaseAndIsDeletedFalseOrderByCreatedAtDesc(String keyword);
	
	List<ProductEntity> findByProductCategory_CategoryNoAndIsDeletedFalseOrderByCreatedAtDesc(Long categoryNo);
	List<ProductEntity> findByProductNameContainingIgnoreCaseAndProductCategory_CategoryNoAndIsDeletedFalseOrderByCreatedAtDesc(String keyword, Long categoryNo);

	
    // 페이징으로 전체 상품 조회, 최신순 정렬 기본
    //Page<ProductEntity> findAllByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
    
}
