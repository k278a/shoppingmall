package com.personal.shoppingmall.order.dto;

import lombok.Getter;

@Getter
public class OrderDetailResponseDto {

    private final Long id;        
    private final Long productId;
    private final Long price;
    private final int orderNumber;

    public OrderDetailResponseDto(Long id, Long productId, Long price, int orderNumber) {
        this.id = id;
        this.productId = productId;
        this.price = price;
        this.orderNumber = orderNumber;
    }

}
