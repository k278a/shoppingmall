package com.personal.shoppingmall.order.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderResponseDto {

    private final Long id;
    private final String userEmail;  // 수정: String으로 변경
    private final Long totalPrice;
    private final String orderStatus;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<OrderDetailResponseDto> orderDetails;

    public OrderResponseDto(Long id, String userEmail, Long totalPrice, String orderStatus, LocalDateTime createdAt, LocalDateTime updatedAt, List<OrderDetailResponseDto> orderDetails) {
        this.id = id;
        this.userEmail = userEmail;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.orderDetails = orderDetails;
    }

}
