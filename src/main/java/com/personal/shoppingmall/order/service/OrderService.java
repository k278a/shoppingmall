package com.personal.shoppingmall.order.service;

import com.personal.shoppingmall.exception.CustomException;
import com.personal.shoppingmall.exception.ErrorCodes;
import com.personal.shoppingmall.order.dto.OrderDetailResponseDto;
import com.personal.shoppingmall.order.dto.OrderRequestDto;
import com.personal.shoppingmall.order.dto.OrderResponseDto;
import com.personal.shoppingmall.order.entity.Order;
import com.personal.shoppingmall.order.entity.OrderDetail;
import com.personal.shoppingmall.order.repository.OrderDetailRepository;
import com.personal.shoppingmall.order.repository.OrderRepository;
import com.personal.shoppingmall.product.entity.Product;
import com.personal.shoppingmall.product.repository.ProductRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.personal.shoppingmall.exception.ErrorCodes.ORDER_NOT_FOUND;

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
                "준비중",
                "준비중",
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

    @Transactional
    public void updateOrderStatus(Long orderId, String orderStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND,"Order not found"));

        order.updateOrderStatus(orderStatus);
        orderRepository.save(order);
    }

    @Transactional
    public void updateDeliveryStatus(Long orderId, String deliveryStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND,"Order not found"));

        order.updateDeliveryStatus(deliveryStatus);
        orderRepository.save(order);
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) // 24시간마다 실행
    public void autoUpdateDeliveryStatus() {
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);

        List<Order> orders = orderRepository.findByDeliveryStatusAndUpdatedAtBefore("배송중", twoDaysAgo);

        for (Order order : orders) {
            order.updateDeliveryStatus("배송 완료");
            orderRepository.save(order);
        }
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND,"Order not found"));

        // 상태가 "Pending"이거나 배송 완료 후 1일 이내인 경우에만 취소 가능
        boolean isCancelable = "준비중".equals(order.getOrderStatus()) ||
                ("배송 완료".equals(order.getDeliveryStatus()) && order.getCreatedAt().isAfter(LocalDateTime.now().minusDays(1)));

        if (isCancelable) {
            order.updateOrderStatus("취소됨");
            orderRepository.save(order);
        } else {
            throw new CustomException(ErrorCodes.ORDER_CREATION_FAILED, "Order cannot be canceled. It may be past the allowed cancel period or already processed.");

        }
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
