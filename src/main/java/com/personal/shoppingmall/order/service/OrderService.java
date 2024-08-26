package com.personal.shoppingmall.order.service;



import com.personal.shoppingmall.order.dto.OrderDetailResponseDto;
import com.personal.shoppingmall.order.dto.OrderRequestDto;
import com.personal.shoppingmall.order.dto.OrderResponseDto;
import com.personal.shoppingmall.order.entity.Order;
import com.personal.shoppingmall.order.entity.OrderDetail;
import com.personal.shoppingmall.order.repository.OrderDetailRepository;
import com.personal.shoppingmall.order.repository.OrderRepository;
import com.personal.shoppingmall.product.entity.Product;
import com.personal.shoppingmall.product.repository.ProductRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        // 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // 주문 생성
        Order order = new Order(
                userEmail,
                calculateTotalPrice(orderRequestDto),
                "Pending",
                "Not Shipped",
                LocalDateTime.now(),
                LocalDateTime.now(),
                List.of()  // 초기 주문 세부사항 리스트는 빈 상태
        );

        // 주문 저장
        orderRepository.save(order);

        // 주문 세부사항 처리 및 저장
        for (var detailDto : orderRequestDto.getOrderDetails()) {
            Product product = productRepository.findById(detailDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderDetail orderDetail = new OrderDetail(
                    order,
                    product,
                    product.getPrice(),
                    detailDto.getOrderNumber()
            );

            orderDetailRepository.save(orderDetail);  // 세부사항 저장
        }

        // 저장된 주문과 관련된 세부사항 조회
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(order.getId());

        // OrderResponseDto 생성
        List<OrderDetailResponseDto> orderDetailDtos = orderDetails.stream()
                .map(detail -> new OrderDetailResponseDto(
                        detail.getId(),
                        detail.getProduct().getId(),
                        detail.getPrice(),
                        detail.getOrderNumber()
                ))
                .collect(Collectors.toList());

        return new OrderResponseDto(
                order.getId(),
                order.getUserEmail(),
                order.getTotalPrice(),
                order.getOrderStatus(),
                order.getDeliveryStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                orderDetailDtos
        );
    }



    private long calculateTotalPrice(OrderRequestDto orderRequestDto) {
        return orderRequestDto.getOrderDetails().stream()
                .mapToLong(detailDto -> {
                    Product product = productRepository.findById(detailDto.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found"));
                    return product.getPrice() * detailDto.getOrderNumber();
                })
                .sum();
    }
}
