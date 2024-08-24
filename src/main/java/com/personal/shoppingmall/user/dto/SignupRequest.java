package com.personal.shoppingmall.user.dto;

import lombok.Getter;

@Getter
public class SignupRequest {

    private final String name;
    private final String email;
    private final String password;
    private final String confirmPassword;
    private final String phoneNumber;
    private final String address;

    public SignupRequest(String name, String email, String password, String confirmPassword, String phoneNumber, String address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

}
