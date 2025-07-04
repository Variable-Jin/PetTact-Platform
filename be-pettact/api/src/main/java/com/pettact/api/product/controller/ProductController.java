package com.pettact.api.product.controller;

import java.util.List;

import com.pettact.api.multiFile.entity.MultiFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.pettact.api.product.dto.ProductCreateDTO;
import com.pettact.api.product.dto.ProductDTO;
import com.pettact.api.product.dto.ProductDetailDTO;
import com.pettact.api.product.dto.ProductUpdateDTO;
import com.pettact.api.product.service.ProductService;
import com.pettact.api.security.vo.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
	
	private final ProductService productService;
	//단일 상품 조회, 페이징 처리
	
	// 상품 상세보기 
	@GetMapping("/list/{productNo}")
	public ResponseEntity <ProductDetailDTO> getProductDetail(@PathVariable("productNo") Long productNo) {
	    ProductDetailDTO myProduct = productService.getProductDetail(productNo);
	    return ResponseEntity.ok(myProduct);
	}

	// 상품 목록
	@GetMapping("/list")
	public ResponseEntity<List<ProductDTO>> getAllProduct(){
		List<ProductDTO> products = productService.getAllProduct();
		return ResponseEntity.ok(products);
	}
	
    // 상품 삭제 (논리 삭제)
    @PutMapping("/delete/{productNo}")
    public ResponseEntity<String> deleteProduct(@PathVariable("productNo") Long productNo, @AuthenticationPrincipal CustomUserDetails user) throws Exception{
    	try {
            productService.deleteProduct(productNo, user.getUser());
            return ResponseEntity.ok("상품이 삭제되었습니다.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("상품을 찾을 수 없습니다.");
        }
    }
    
    // 상품 수정 
    @PutMapping("/update/{productNo}")
    public ResponseEntity<String> updateProduct(@PathVariable("productNo") Long productNo, @RequestBody @Valid ProductUpdateDTO dto, @AuthenticationPrincipal CustomUserDetails user){
    	productService.updateProduct(productNo, dto, user);
    	return ResponseEntity.ok("상품이 수정되었습니다.");
    }

    // 상품 등록
    @PostMapping(value = "/create")
    public ResponseEntity<String> createProduct(
            //@RequestPart("product") @Valid ProductCreateDTO dto,
            @RequestPart("product") String productJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails user) {

        productService.createProduct(productJson, files, user);
        return ResponseEntity.ok("상품이 등록되었습니다.");
    }

}
