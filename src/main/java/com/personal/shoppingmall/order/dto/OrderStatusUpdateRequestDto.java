package com.personal.shoppingmall.order.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class OrderStatusUpdateRequestDto {

    private final String orderStatus;

    @JsonCreator
    public OrderStatusUpdateRequestDto(@JsonProperty("orderStatus") String orderStatus) {
        this.orderStatus = orderStatus;
    }

}
