package com.personal.shoppingmall.order.repository;



import com.personal.shoppingmall.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // 필요에 따라 추가 쿼리 메서드 정의 가능
}