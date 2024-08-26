package com.personal.shoppingmall.order.dto;

import lombok.Getter;

@Getter
public class OrderDetailRequestDto {

    private Long productId;
    private int orderNumber;

    public OrderDetailRequestDto() {
    }

    public OrderDetailRequestDto(Long productId, int orderNumber) {
        this.productId = productId;
        this.orderNumber = orderNumber;
    }

}
