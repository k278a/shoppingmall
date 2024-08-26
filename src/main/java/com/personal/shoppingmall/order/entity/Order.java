package com.personal.shoppingmall.order.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Table(name = "orders")  // 예약어와 충돌하지 않는 이름으로 테이블 지정
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;
    private Long totalPrice;
    private String orderStatus;
    private String deliveryStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    public Order() {
    }

    public Order(String userEmail, Long totalPrice, String orderStatus, String deliveryStatus, LocalDateTime createdAt, LocalDateTime updatedAt, List<OrderDetail> orderDetails) {
        this.userEmail = userEmail;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.deliveryStatus = deliveryStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.orderDetails = orderDetails;
    }

}
