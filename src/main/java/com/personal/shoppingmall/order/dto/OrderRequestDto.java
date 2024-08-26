package com.personal.shoppingmall.order.dto;

import java.util.List;

public class OrderRequestDto {

    private List<OrderDetailRequestDto> orderDetails;

    public OrderRequestDto() {
    }

    public OrderRequestDto(List<OrderDetailRequestDto> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public List<OrderDetailRequestDto> getOrderDetails() {
        return orderDetails;
    }
}
