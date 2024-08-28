package com.personal.shoppingmall.wishlist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WishListOrderResponseDto {
    private Long orderId;
    private String orderStatus;
    private Long totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<WishListOrderRequestDto> orderDetails;  // 수정된 부분
}
