package com.personal.shoppingmall.security.util;

import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class EncryptionService {

    // 일반 텍스트 암호화 (Base64 인코딩)
    public String encrypt(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes());
    }

    // 일반 텍스트 복호화 (Base64 디코딩)
    public String decrypt(String encryptedText) {
        return new String(Base64.getDecoder().decode(encryptedText));
    }
}
