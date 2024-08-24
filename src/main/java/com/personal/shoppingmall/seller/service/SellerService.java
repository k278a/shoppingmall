package com.personal.shoppingmall.seller.service;

import com.personal.shoppingmall.exception.CustomException;
import com.personal.shoppingmall.exception.ErrorCodes;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.personal.shoppingmall.seller.dto.SellerSignupRequestDto;
import com.personal.shoppingmall.seller.dto.SellerSignupResponseDto;
import com.personal.shoppingmall.seller.entity.Seller;
import com.personal.shoppingmall.seller.repository.SellerRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

@Service
public class SellerService {

    private static final Logger logger = LoggerFactory.getLogger(SellerService.class);

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;


    public SellerService(SellerRepository sellerRepository, PasswordEncoder passwordEncoder) {
        this.sellerRepository = sellerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public SellerSignupResponseDto signupSeller(SellerSignupRequestDto request) {
        logger.info("판매자 회원가입 요청 수신: {}", request);

        // 비밀번호 일치 확인
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            logger.warn("비밀번호 불일치: 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
            throw new CustomException(ErrorCodes.SELLER_PASSWORD_MISMATCH, "비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        // 이메일 중복 확인
        if (sellerRepository.findByEmail(request.getEmail()).isPresent()) {
            logger.warn("이메일 중복: {}", request.getEmail());
            throw new CustomException(ErrorCodes.SELLER_EMAIL_ALREADY_SIGNED_UP, "이 이메일 주소는 이미 사용 중입니다.");
        }

        // 비밀번호 검증
        if (!isValidPassword(request.getPassword())) {
            logger.warn("비밀번호 검증 실패: 비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다.");
            throw new CustomException(ErrorCodes.SELLER_PASSWORD_VALIDATION_FAILED, "비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다.");
        }

        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(request.getPassword());
        // 기타 필드 암호화
        String encryptedBusinessNumber = request.getBusinessNumber(); // 비즈니스 번호 암호화 로직 필요
        String encryptedBusinessName = request.getBusinessName(); // 비즈니스 이름 암호화 로직 필요
        String encryptedBusinessAddress = request.getBusinessAddress(); // 비즈니스 주소 암호화 로직 필요

        // Seller 객체 생성 및 저장
        Seller seller = Seller.builder()
                .email(request.getEmail())
                .encryptedPassword(encryptedPassword)
                .encryptedBusinessNumber(encryptedBusinessNumber)
                .encryptedBusinessName(encryptedBusinessName)
                .encryptedBusinessAddress(encryptedBusinessAddress)
                .build();

        sellerRepository.save(seller);

        return new SellerSignupResponseDto("회원가입 성공");
    }

    // 비밀번호 유효성 검사
    private boolean isValidPassword(String password) {
        // 비밀번호는 최소 8자 이상, 대문자, 소문자, 숫자, 특수문자를 포함해야 함
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return Pattern.compile(regex).matcher(password).matches();
    }
}
