package com.personal.shoppingmall.email.repository;



import com.personal.shoppingmall.email.entity.UserEmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserEmailVerificationTokenRepository extends JpaRepository<UserEmailVerificationToken, Long> {

    Optional<UserEmailVerificationToken> findByToken(String token);

    // 다른 쿼리 메서드 추가 가능
}
