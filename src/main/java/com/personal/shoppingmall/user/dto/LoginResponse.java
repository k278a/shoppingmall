package com.personal.shoppingmall.user.dto;


import lombok.Getter;

@Getter
public class LoginResponse {

    private final String message;

    public LoginResponse(String message) {
        this.message = message;
    }

}
