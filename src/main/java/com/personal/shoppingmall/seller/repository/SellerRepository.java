package com.personal.shoppingmall.seller.repository;

import com.personal.shoppingmall.seller.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Object> findByEmail(String email);
}
