package com.personal.shoppingmall.seller.entity;

import com.personal.shoppingmall.security.util.EncryptionService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder(toBuilder = true)
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String encryptedPassword;
    private String encryptedBusinessNumber;
    private String encryptedBusinessName;
    private String encryptedBusinessAddress;


    // 더 이상 인증 상태와 역할 관련 메서드 제거됨
}
