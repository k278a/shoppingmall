package com.personal.shoppingmall.seller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SellerUpdateRequestDto {
    private String businessNumber;
    private String businessName;
    private String businessAddress;
}
