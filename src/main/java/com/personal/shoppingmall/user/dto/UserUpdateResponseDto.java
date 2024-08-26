package com.personal.shoppingmall.user.dto;

import lombok.Getter;

@Getter
public class UserUpdateResponseDto {
    private final String message;

    public UserUpdateResponseDto(String message) {
        this.message = message;
    }

}
