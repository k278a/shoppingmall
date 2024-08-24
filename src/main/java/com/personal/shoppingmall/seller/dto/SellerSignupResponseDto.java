package com.personal.shoppingmall.seller.dto;

import lombok.Getter;

@Getter
public class SellerSignupResponseDto {
    // Getter
    private final String message;

    // Constructor
    public SellerSignupResponseDto(String message) {
        this.message = message;
    }

}
