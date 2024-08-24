package com.personal.shoppingmall.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public User(String name, String email, String password, String phoneNumber, String address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.verified = false; // 기본값으로 인증되지 않은 상태
    }

    public void updateVerifiedStatus(boolean status) {
        this.verified = status;
    }
}
