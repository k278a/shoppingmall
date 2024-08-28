package com.personal.shoppingmall.wishlist.dto;

import lombok.Getter;

@Getter
public class WishListOrderRequestDto {
    private final Long itemId;
    private final int quantity;

    public WishListOrderRequestDto(Long itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }
}
