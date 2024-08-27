package com.personal.shoppingmall.order.repository;

import com.personal.shoppingmall.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrderStatusAndUpdatedAtBefore(String orderStatus, LocalDateTime updatedAt);
    List<Order> findByOrderStatusAndCreatedAtBefore(String orderStatus, LocalDateTime createdAt);

    // 필요에 따라 추가 쿼리 메서드 정의 가능
}
