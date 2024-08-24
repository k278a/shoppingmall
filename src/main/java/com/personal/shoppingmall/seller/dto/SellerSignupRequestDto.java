package com.personal.shoppingmall.seller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SellerSignupRequestDto {
    private String email;
    private String password;
    private String confirmPassword; // 추가된 필드
    private String businessNumber;
    private String businessName;
    private String businessAddress;
}
