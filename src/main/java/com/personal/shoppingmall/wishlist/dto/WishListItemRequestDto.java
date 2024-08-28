package com.personal.shoppingmall.wishlist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WishListItemRequestDto {
    private Long productId;
    private int quantity;
}
