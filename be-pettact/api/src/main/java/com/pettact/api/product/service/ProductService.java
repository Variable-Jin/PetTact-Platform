package com.pettact.api.product.service;


import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pettact.api.multiFile.dto.FileCreateDto;
import com.pettact.api.multiFile.entity.MultiFile;
import com.pettact.api.multiFile.service.FileService;
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
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
	
	private final ProductRepository productRepository;
	private final ProductCategoryRepository categoryRepository;
	private final FileService fileService;
	private final ObjectMapper objectMapper;

	//상품 상세보기
	public ProductDetailDTO getProductDetail (Long productNo) {
		
	    ProductEntity product = productRepository.findById(productNo)
	    		 .orElseThrow(() -> new RuntimeException("해당 상품을 찾을 수 없습니다."));

	    if (product.isDeleted()) {
	        throw new RuntimeException("삭제된 상품입니다.");
	    }

		List<MultiFile> files = fileService.getFilesByReference(
				MultiFile.ReferenceTable.PRODUCT, productNo);
	    
	    return ProductDetailDTO.builder()
	    		.productNo(product.getProductNo())
	            .productName(product.getProductName())
	            .productDescription(product.getProductDescription())
	            .productPrice(product.getProductPrice())
	            .productStock(product.getProductStock())
	            .categoryName(product.getProductCategory().getCategoryName())
	            .createdAt(product.getCreatedAt())
	            .status(product.isProductStatus())
	            .userNo(product.getUser().getUserNo())
	            .userName(product.getUser().getUserName())
				.files(files)
	            .build();
	}

	
	//상품 목록 
	public List<ProductDTO> getAllProduct(){
		
		List<ProductEntity> productList = productRepository.findAll()
		        .stream()
		        .filter(product -> !product.isDeleted()) // 삭제되지 않은 상품만 목록에 가져옴
		        .collect(Collectors.toList());
		System.out.println("조회된 상품 수: " + productList.size()); // 로그 찍기
		return productList.stream().map(product -> ProductDTO.builder()
	            .productNo(product.getProductNo())
	            .productName(product.getProductName())
	            .productDescription(product.getProductDescription())
	            .productPrice(product.getProductPrice())
	            .productStock(product.getProductStock())
	            .createdAt(product.getCreatedAt())
	            .categoryNo(product.getProductCategory().getCategoryNo())
	            .categoryName(product.getProductCategory().getCategoryName())
	            .status(product.isProductStatus())
	            .build())
	        .collect(Collectors.toList());
	}
	
	//상품 삭제
	public void deleteProduct(Long productNo ,Users user) throws AccessDeniedException {
		
	    ProductEntity product = productRepository.findByProductNo(productNo)
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
	    ProductCategoryEntity category = categoryRepository.findById(dto.getCategoryNo())
	            .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));
		
		//기존 상품 정보 수정
		product.setProductName(dto.getProductName());
		product.setProductDescription(dto.getProductDescription());
		product.setProductPrice(dto.getProductPrice());
		product.setProductStock(dto.getProductStock());
		product.setProductCategory(category);
		product.prePersist();
		product.setProductStatus(dto.isProductStatus());
		
		productRepository.save(product); // 레포지토리에 저장
	}


	//상품 등록
	public void createProduct(String productJson, List<MultipartFile> files, CustomUserDetails user) {
		try {
			// 1. JSON 파싱
			ProductCreateDTO dto = objectMapper.readValue(productJson, ProductCreateDTO.class);

			// 2. 카테고리 조회
			ProductCategoryEntity category = categoryRepository.findById(dto.getCategoryNo())
					.orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));

			// 3. 상품 생성
			ProductEntity product = ProductEntity.builder()
					.productName(dto.getProductName())
					.productDescription(dto.getProductDescription())
					.productPrice(dto.getProductPrice())
					.productStock(dto.getProductStock())
					.productCategory(category)
					.productStatus(dto.isProductStatus())
					.user(user.getUserEntity())
					.build();

			// 4. 상품 저장
			ProductEntity savedProduct = productRepository.save(product);

			// 5. 파일 저장
			if (files != null && !files.isEmpty()) {
				for (MultipartFile uploadFile : files) {
					FileCreateDto createDto = new FileCreateDto();
					createDto.setReferenceTable(MultiFile.ReferenceTable.PRODUCT);
					createDto.setReferenceNo(savedProduct.getProductNo());

					fileService.createFile(createDto, uploadFile, user.getUser().getUserNo());
				}
			}

		} catch (JsonProcessingException e) {
			throw new RuntimeException("JSON 파싱 오류: " + e.getMessage(), e);
		}
	}
}

