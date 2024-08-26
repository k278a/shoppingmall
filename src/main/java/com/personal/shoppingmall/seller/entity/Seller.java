package com.personal.shoppingmall.seller.entity;

import com.personal.shoppingmall.security.entity.RoleName;
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
    private boolean verified; // 인증 상태

    @Enumerated(EnumType.STRING)
    private RoleName role; // 권한 역할

    public void updateBusinessDetails(String encryptedBusinessNumber, String encryptedBusinessName, String encryptedBusinessAddress) {
        this.encryptedBusinessNumber = encryptedBusinessNumber;
        this.encryptedBusinessName = encryptedBusinessName;
        this.encryptedBusinessAddress = encryptedBusinessAddress;
    }

    public void updateVerifiedStatus(boolean verified) {
        this.verified = verified;
    }

    public RoleName getRole() {
        return role;
    }

    public String getRoleAuthority() {
        return role.getAuthority();
    }
}
