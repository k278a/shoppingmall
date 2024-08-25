package com.personal.shoppingmall.seller.dto;

import lombok.Getter;

@Getter
public class SellerResponseDto {
    // Getters and setters
    private String email;
    private String businessNumber;
    private String businessName;
    private String businessAddress;

    public SellerResponseDto(String email, String businessNumber, String businessName, String businessAddress) {
        this.email = email;
        this.businessNumber = businessNumber;
        this.businessName = businessName;
        this.businessAddress = businessAddress;
    }
}
