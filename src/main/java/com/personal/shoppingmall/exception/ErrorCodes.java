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
    public static final String USER_NOT_FOUND = USER_ERROR_PREFIX + "USER_NOT_FOUND";
    public static final String EMAIL_NOT_VERIFIED = USER_ERROR_PREFIX + "EMAIL_NOT_VERIFIED";
    public static final String INVALID_PASSWORD = USER_ERROR_PREFIX + "INVALID_PASSWORD";

    // 사용자 입력 형식 오류 코드
    public static final String USER_INPUT_ERROR_PREFIX = "USER_INPUT_ERROR_";
    public static final String INVALID_EMAIL_FORMAT = USER_INPUT_ERROR_PREFIX + "INVALID_EMAIL_FORMAT";
    public static final String INVALID_PHONE_NUMBER_FORMAT = USER_INPUT_ERROR_PREFIX + "INVALID_PHONE_NUMBER_FORMAT";

    // 판매자 관련 오류 코드
    public static final String SELLER_ERROR_PREFIX = "SELLER_ERROR_";
    public static final String SELLER_EMAIL_ALREADY_SIGNED_UP = SELLER_ERROR_PREFIX + "EMAIL_ALREADY_SIGNED_UP";
    public static final String SELLER_PASSWORD_MISMATCH = SELLER_ERROR_PREFIX + "PASSWORD_MISMATCH";
    public static final String SELLER_PASSWORD_VALIDATION_FAILED = SELLER_ERROR_PREFIX + "PASSWORD_VALIDATION_FAILED";
    public static final String SELLER_NOT_FOUND = SELLER_ERROR_PREFIX + "SELLER_NOT_FOUND";

    // 제품 관련 오류 코드
    public static final String PRODUCT_ERROR_PREFIX = "PRODUCT_ERROR_";
    public static final String PRODUCT_ALREADY_EXISTS = PRODUCT_ERROR_PREFIX + "PRODUCT_ALREADY_EXISTS";
    public static final String PRODUCT_NOT_FOUND = PRODUCT_ERROR_PREFIX + "PRODUCT_NOT_FOUND";
    public static final String OUT_OF_STOCK = PRODUCT_ERROR_PREFIX + "OUT_OF_STOCK";

    // 주문 관련 오류 코드
    public static final String ORDER_ERROR_PREFIX = "ORDER_ERROR_";
    public static final String ORDER_CREATION_FAILED = ORDER_ERROR_PREFIX + "CREATION_FAILED";
    public static final String ORDER_NOT_FOUND = ORDER_ERROR_PREFIX + "NOT_FOUND";

    // 이메일 인증 관련 오류 코드
    public static final String INVALID_TOKEN = "INVALID_TOKEN";

    // 위시리스트 관련 오류 코드
    public static final String WISHLIST_ERROR_PREFIX = "WISHLIST_ERROR_";
    public static final String WISHLIST_NOT_FOUND = WISHLIST_ERROR_PREFIX + "WISHLIST_NOT_FOUND";
    public static final String WISHLIST_ITEM_NOT_FOUND = WISHLIST_ERROR_PREFIX + "WISHLIST_ITEM_NOT_FOUND";
    public static final String INVALID_QUANTITY = WISHLIST_ERROR_PREFIX + "INVALID_QUANTITY"; // 수량 오류 코드

    // 새로운 오류 코드가 필요하면 여기에 추가
}
