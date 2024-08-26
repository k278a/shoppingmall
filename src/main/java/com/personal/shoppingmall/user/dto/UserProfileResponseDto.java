package com.personal.shoppingmall.user.dto;

import lombok.Getter;

@Getter
public class UserProfileResponseDto {
    private final String name;
    private final String email;
    private final String phoneNumber;
    private final String address;

    public UserProfileResponseDto(String name, String email, String phoneNumber, String address) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
}
