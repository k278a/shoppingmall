package com.personal.shoppingmall.user.entity;

import com.personal.shoppingmall.security.entity.RoleName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // 암호화된 비밀번호

    private String phoneNumber; // 암호화된 전화번호
    private String address; // 암호화된 주소

    private boolean verified;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleName role; // 역할 추가

    public User(String name, String email, String password, String phoneNumber, String address, RoleName role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.verified = false; // 기본값으로 인증되지 않은 상태
        this.role = role; // 역할 설정
    }

    public void updateVerifiedStatus(boolean status) {
        this.verified = status;
    }

    // 추가: 역할을 반환하는 메서드
    public List<RoleName> getRoles() {
        return Collections.singletonList(role);
    }
}
