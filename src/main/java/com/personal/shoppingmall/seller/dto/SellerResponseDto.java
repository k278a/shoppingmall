package com.personal.shoppingmall.seller.dto;

import lombok.Getter;

@Getter
public class SellerResponseDto {
    // Getters and setters
    private final String email;
    private final String businessNumber;
    private final String businessName;
    private final String businessAddress;

    public SellerResponseDto(String email, String businessNumber, String businessName, String businessAddress) {
        this.email = email;
        this.businessNumber = businessNumber;
        this.businessName = businessName;
        this.businessAddress = businessAddress;
    }
}
