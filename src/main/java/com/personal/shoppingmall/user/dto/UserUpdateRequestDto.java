package com.personal.shoppingmall.user.dto;

import lombok.Getter;

@Getter
public class UserUpdateRequestDto {
    private final String name;
    private final String phoneNumber;
    private final String address;

    public UserUpdateRequestDto(String name, String phoneNumber, String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

}
