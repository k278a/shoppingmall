package com.personal.shoppingmall.security.entity;

public enum RoleName {
    USER("ROLE_USER"),
    SELLER("ROLE_SELLER");

    private final String authority;

    RoleName(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
}
