package com.personal.shoppingmall.email.repository;

import com.personal.shoppingmall.email.entity.SellerEmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerEmailVerificationTokenRepository extends JpaRepository<SellerEmailVerificationToken, Long> {
    Optional<SellerEmailVerificationToken> findByToken(String token);
}
