package com.personal.shoppingmall.order.repository;


import com.personal.shoppingmall.order.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrderId(Long id);
    // 필요에 따라 추가 쿼리 메서드 정의 가능
}