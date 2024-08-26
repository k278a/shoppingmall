package com.personal.shoppingmall.order.controller;

import com.personal.shoppingmall.order.dto.OrderRequestDto;
import com.personal.shoppingmall.order.dto.OrderResponseDto;
import com.personal.shoppingmall.order.entity.Order;
import com.personal.shoppingmall.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto orderRequestDto) {
        // 주문 생성 및 OrderResponseDto 반환
        OrderResponseDto orderResponseDto = orderService.createOrder(orderRequestDto);

        // 응답 상태 코드 201(Created)과 함께 OrderResponseDto 반환
        return new ResponseEntity<>(orderResponseDto, HttpStatus.CREATED);
    }

}
