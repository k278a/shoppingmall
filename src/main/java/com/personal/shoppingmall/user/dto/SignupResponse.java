package com.personal.shoppingmall.user.dto;

import lombok.Getter;

@Getter
public class SignupResponse {

    private final String message;

    public SignupResponse(String message) {
        this.message = message;
    }

}
