package com.personal.shoppingmall.product.dto;

import lombok.Getter;

@Getter
public class ProductResponseDto {

    // Getter 메서드들
    private final Long id;
    private final String productName;
    private final String productDescription;
    private final int productStock;
    private final Long price;
    private final String categoryname;

    // 모든 필드를 포함한 생성자
    public ProductResponseDto(Long id, String productName, String productDescription, int productStock, Long price, String categoryname) {
        this.id = id;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productStock = productStock;
        this.price = price;
        this.categoryname = categoryname;
    }

}