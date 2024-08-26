package com.personal.shoppingmall.product.dto;


import lombok.Getter;

@Getter
public class ProductRequestDto {

    // Getter 메서드들
    private final String productName;
    private final String productDescription;
    private final int productStock;
    private final Long price;
    private final String categoryname;


    // 모든 필드를 포함한 생성자
    public ProductRequestDto(String productName, String productDescription, int productStock, Long price, String categoryname) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.productStock = productStock;
        this.price = price;
        this.categoryname = categoryname;
    }

}
