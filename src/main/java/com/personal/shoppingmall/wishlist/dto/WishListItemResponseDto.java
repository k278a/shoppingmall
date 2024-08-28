package com.personal.shoppingmall.wishlist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class WishListItemResponseDto {
    private Long id;
    private String productName;
    private String productDescription;
    private Long price;
    private int quantity;
}
