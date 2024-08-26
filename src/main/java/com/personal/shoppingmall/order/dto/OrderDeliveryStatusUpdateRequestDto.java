package com.personal.shoppingmall.order.dto;

import lombok.Getter;

@Getter
public class OrderDeliveryStatusUpdateRequestDto {

    // Getter
    private String deliveryStatus;

    // 기본 생성자
    public OrderDeliveryStatusUpdateRequestDto() {
    }

    // 생성자
    public OrderDeliveryStatusUpdateRequestDto(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

}
