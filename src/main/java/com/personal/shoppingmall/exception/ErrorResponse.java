package com.personal.shoppingmall.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final int status;
    private final String errorCode;
    private final String message;

    public ErrorResponse(int status, String errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }

}
