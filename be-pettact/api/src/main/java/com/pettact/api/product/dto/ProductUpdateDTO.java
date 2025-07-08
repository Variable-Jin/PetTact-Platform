package com.pettact.api.product.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateDTO { 
	
	//상품 수정 전용 DTO
	@NotBlank(message = "상품 이름은 필수입니다.")
    private String productName;
	
	@NotBlank(message = "상품 설명은 필수입니다.")
    private String productDescription;
	
    @NotNull(message = "가격을 입력하세요.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer productPrice;
    
    @NotNull(message = "재고 수량을 입력하세요.")
    @Min(value = 0, message = "수량은 0 이상이어야 합니다.")
    private Integer productStock;
    
    @NotNull(message = "카테고리를 입력하세요.")
    private Long categoryNo; //카테고리 id
    
    private boolean productStatus;
    
    //private String imageUrl;
    
    private List<String> imageUrls;

}
