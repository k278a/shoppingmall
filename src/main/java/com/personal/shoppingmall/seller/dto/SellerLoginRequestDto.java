package com.personal.shoppingmall.seller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SellerLoginRequestDto {
    private final String email;
    private final String password;
}
