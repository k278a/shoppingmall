package com.personal.shoppingmall.exception;

public class ErrorCodes {

    // 일반 오류 코드
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    // 사용자 관련 오류 코드
    public static final String USER_ERROR_PREFIX = "USER_ERROR_";
    public static final String USER_ALREADY_EXISTS = USER_ERROR_PREFIX + "USER_ALREADY_EXISTS";
    public static final String PASSWORD_MISMATCH = USER_ERROR_PREFIX + "PASSWORD_MISMATCH";
    public static final String PASSWORD_VALIDATION_FAILED = USER_ERROR_PREFIX + "PASSWORD_VALIDATION_FAILED";

    // 사용자 입력 형식 오류 코드
    public static final String USER_INPUT_ERROR_PREFIX = "USER_INPUT_ERROR_";
    public static final String INVALID_EMAIL_FORMAT = USER_INPUT_ERROR_PREFIX + "INVALID_EMAIL_FORMAT";
    public static final String INVALID_PHONE_NUMBER_FORMAT = USER_INPUT_ERROR_PREFIX + "INVALID_PHONE_NUMBER_FORMAT";

    // 추가적인 오류 코드는 여기에 필요에 따라 추가할 수 있습니다
}
