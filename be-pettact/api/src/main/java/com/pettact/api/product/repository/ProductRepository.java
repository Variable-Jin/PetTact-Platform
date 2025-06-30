package com.pettact.api.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pettact.api.product.entity.ProductEntity;
import com.pettact.api.user.entity.Users;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>{
	
	List<ProductEntity> findByUser(Users user);
	
	Optional<ProductEntity> findByProductNo(Long productNo);

}
