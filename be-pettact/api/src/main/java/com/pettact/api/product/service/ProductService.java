package com.pettact.api.product.service;


import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pettact.api.product.dto.ProductCreateDTO;
import com.pettact.api.product.dto.ProductDTO;
import com.pettact.api.product.dto.ProductDetailDTO;
import com.pettact.api.product.dto.ProductUpdateDTO;
import com.pettact.api.product.entity.ProductCategoryEntity;
import com.pettact.api.product.entity.ProductEntity;
import com.pettact.api.product.repository.ProductCategoryRepository;
import com.pettact.api.product.repository.ProductRepository;
import com.pettact.api.security.vo.CustomUserDetails;
import com.pettact.api.user.entity.Users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
	
	private final ProductRepository productRepository;
	private final ProductCategoryRepository categoryRepository;

	//상품 상세보기
	public ProductDetailDTO getProductDetail (Long productNo) {
		
	    ProductEntity product = productRepository.findById(productNo)
	    		 .orElseThrow(() -> new RuntimeException("해당 상품을 찾을 수 없습니다."));

	    if (product.isDeleted()) {
	        throw new RuntimeException("삭제된 상품입니다.");
	    }
	    
	    return ProductDetailDTO.builder()
	    		.productsNo(product.getProductsNo())
	            .productsName(product.getProductsName())
	            .productsDescription(product.getProductsDescription())
	            .productsPrice(product.getProductsPrice())
	            .productsStock(product.getProductsStock())
	            .categoryName(product.getProductsCategory().getCategoryName())
	            .createdAt(product.getCreatedAt())
	            .status(product.isProductsStatus())
	            .userNo(product.getUser().getUserNo())
	            .userName(product.getUser().getUserName())
	            .build();
	}

	
	//상품 목록 
	public List<ProductDTO> getAllProducts(){
		
		List<ProductEntity> productList = productRepository.findAll()
		        .stream()
		        .filter(product -> !product.isDeleted()) // 삭제되지 않은 상품만 목록에 가져옴
		        .collect(Collectors.toList());
		System.out.println("조회된 상품 수: " + productList.size()); // 로그 찍기
		return productList.stream().map(product -> ProductDTO.builder()
	            .productsNo(product.getProductsNo())
	            .productsName(product.getProductsName())
	            .productsDescription(product.getProductsDescription())
	            .productsPrice(product.getProductsPrice())
	            .productsStock(product.getProductsStock())
	            .createdAt(product.getCreatedAt())
	            .categoryId(product.getProductsCategory().getCategoryId())
	            .categoryName(product.getProductsCategory().getCategoryName())
	            .status(product.isProductsStatus())
	            .build())
	        .collect(Collectors.toList());
	}
	
	//상품 삭제
	public void deleteProduct(Long productNo ,Users user) throws AccessDeniedException {
		
	    ProductEntity product = productRepository.findByProductsNo(productNo)
	            .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
	    log.info("userNo = {} , userNo = {}", product.getUser().getUserNo(),user.getUserNo());
	    if (!product.getUser().getUserNo().equals(user.getUserNo())) {
	        throw new AccessDeniedException("상품을 삭제할 권한이 없습니다.");
	    }
	    
	    product.softDelete(); // 논리 삭제 및 삭제 시간 등록
	    productRepository.save(product);
	}
	
	//상품 수정
	public void updateProduct(Long id, ProductUpdateDTO dto, CustomUserDetails user) {
		ProductEntity product = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
	    ProductCategoryEntity category = categoryRepository.findById(dto.getCategoryId())
	            .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));
		
		//기존 상품 정보 수정
		product.setProductsName(dto.getProductsName());
		product.setProductsDescription(dto.getProductsDescription());
		product.setProductsPrice(dto.getProductsPrice());
		product.setProductsStock(dto.getProductsStock());
		product.setProductsCategory(category);
		product.prePersist();
		product.setProductsStatus(dto.isProductsStatus());
		
		productRepository.save(product); // 레포지토리에 저장
	}
	
	//상품 등록
	public void createProduct(ProductCreateDTO dto, CustomUserDetails user) {
		
	    ProductCategoryEntity category = categoryRepository.findById(dto.getCategoryId())
	            .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));

		ProductEntity product = ProductEntity.builder()
				.productsName(dto.getProductsName())
				.productsDescription(dto.getProductsDescription())
				.productsPrice(dto.getProductsPrice())
				.productsStock(dto.getProductsStock())
				.productsCategory(category) 
				.productsStatus(dto.isProductsStatus())
				.user(user.getUserEntity()) 
				.build();
		
		productRepository.save(product);
	}
	
}
