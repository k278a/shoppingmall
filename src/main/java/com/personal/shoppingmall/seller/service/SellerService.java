package com.personal.shoppingmall.seller.service;

import com.personal.shoppingmall.email.service.EmailService;
import com.personal.shoppingmall.email.service.EmailVerificationService;
import com.personal.shoppingmall.exception.CustomException;
import com.personal.shoppingmall.exception.ErrorCodes;
import com.personal.shoppingmall.security.jwt.JwtTokenProvider;
import com.personal.shoppingmall.security.util.EncryptionService;
import com.personal.shoppingmall.seller.dto.*;
import com.personal.shoppingmall.seller.entity.Seller;
import com.personal.shoppingmall.seller.repository.SellerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.regex.Pattern;

@Service
public class SellerService {

    private static final Logger logger = LoggerFactory.getLogger(SellerService.class);

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;
    private final EncryptionService encryptionService;
    private final JwtTokenProvider jwtTokenProvider;

    public SellerService(SellerRepository sellerRepository, PasswordEncoder passwordEncoder, EmailService emailService, EmailVerificationService emailVerificationService, EncryptionService encryptionService, JwtTokenProvider jwtTokenProvider) {
        this.sellerRepository = sellerRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.emailVerificationService = emailVerificationService;
        this.encryptionService = encryptionService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public SellerSignupResponseDto signupSeller(SellerSignupRequestDto request) {
        logger.info("판매자 회원가입 요청 수신: {}", request);

        // 이메일 유효성 확인
        if (!isEmailValid(request.getEmail())) {
            logger.warn("잘못된 이메일 형식: {}", request.getEmail());
            throw new CustomException(ErrorCodes.INVALID_EMAIL_FORMAT, "잘못된 이메일 형식입니다.");
        }

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
        if (!isPasswordValid(request.getPassword())) {
            logger.warn("비밀번호 검증 실패: 비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다.");
            throw new CustomException(ErrorCodes.SELLER_PASSWORD_VALIDATION_FAILED, "비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다.");
        }

        if (request.getBusinessNumber() != null && !isPhoneNumberValid(request.getBusinessNumber())) {
            logger.warn("잘못된 비즈니스 번호 형식: {}", request.getBusinessNumber());
            throw new CustomException(ErrorCodes.INVALID_PHONE_NUMBER_FORMAT, "잘못된 비즈니스 번호 형식입니다.");
        }

        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        // 암호화
        String encryptedBusinessNumber = encryptionService.encrypt(request.getBusinessNumber());
        String encryptedBusinessName = encryptionService.encrypt(request.getBusinessName());
        String encryptedBusinessAddress = encryptionService.encrypt(request.getBusinessAddress());

        // Seller 객체 생성 및 저장
        Seller seller = Seller.builder()
                .email(request.getEmail())
                .encryptedPassword(encryptedPassword)
                .encryptedBusinessNumber(encryptedBusinessNumber)
                .encryptedBusinessName(encryptedBusinessName)
                .encryptedBusinessAddress(encryptedBusinessAddress)
                .build();

        sellerRepository.save(seller);

        // 이메일 인증 토큰 생성 및 발송
        String token = emailVerificationService.generateVerificationToken(seller);
        emailService.sendVerificationEmail(seller, token);

        logger.info("판매자 회원가입 성공: {} - 인증 이메일을 발송했습니다.", request.getEmail());

        return new SellerSignupResponseDto("회원가입 성공. 인증 이메일을 확인하세요.");
    }

    private boolean isPasswordValid(String password) {
        final String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return Pattern.matches(passwordPattern, password);
    }

    private boolean isEmailValid(String email) {
        final String emailPattern = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
        return Pattern.matches(emailPattern, email);
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        final String phonePattern = "^010\\d{8}$";
        return Pattern.matches(phonePattern, phoneNumber);
    }

    public String verifyEmail(String token) {
        return emailVerificationService.verifyEmail(token);
    }

    public ResponseEntity<SellerLoginResponseDto> loginSeller(SellerLoginRequestDto sellerLoginRequestDto) {
        Seller seller = (Seller) sellerRepository.findByEmail(sellerLoginRequestDto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCodes.SELLER_NOT_FOUND, "판매자를 찾을 수 없습니다."));

        // 이메일 인증 상태 확인
        if (!seller.isVerified()) {
            logger.warn("이메일 인증되지 않음: {}", sellerLoginRequestDto.getEmail());
            throw new CustomException(ErrorCodes.EMAIL_NOT_VERIFIED, "이메일 인증이 필요합니다. 이메일을 확인하세요.");
        }

        if (!passwordEncoder.matches(sellerLoginRequestDto.getPassword(), seller.getEncryptedPassword())) {
            logger.warn("비밀번호 불일치: {}", sellerLoginRequestDto.getPassword());
            throw new CustomException(ErrorCodes.INVALID_PASSWORD, "비밀번호가 잘못되었습니다.");
        }

        String token = jwtTokenProvider.createToken(seller.getEmail());

        logger.info("판매자 로그인 성공: {} - JWT 토큰 생성", seller.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        return new ResponseEntity<>(new SellerLoginResponseDto("로그인 성공"), headers, HttpStatus.OK);
    }

    @Transactional
    public SellerResponseDto updateSeller(String email, SellerUpdateRequestDto sellerUpdateRequestDto) {
        Seller seller = (Seller) sellerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCodes.SELLER_NOT_FOUND, "판매자를 찾을 수 없습니다."));

        // DTO에서 가져온 정보를 암호화
        String encryptedBusinessNumber = encryptionService.encrypt(sellerUpdateRequestDto.getBusinessNumber());
        String encryptedBusinessName = encryptionService.encrypt(sellerUpdateRequestDto.getBusinessName());
        String encryptedBusinessAddress = encryptionService.encrypt(sellerUpdateRequestDto.getBusinessAddress());

        // 엔티티에 암호화된 정보 업데이트
        seller.updateBusinessDetails(encryptedBusinessNumber, encryptedBusinessName, encryptedBusinessAddress);
        Seller updatedSeller = sellerRepository.save(seller);

        // 복호화된 정보를 DTO로 반환
        String decryptedBusinessNumber = encryptionService.decrypt(updatedSeller.getEncryptedBusinessNumber());
        String decryptedBusinessName = encryptionService.decrypt(updatedSeller.getEncryptedBusinessName());
        String decryptedBusinessAddress = encryptionService.decrypt(updatedSeller.getEncryptedBusinessAddress());

        return new SellerResponseDto(
                updatedSeller.getEmail(),
                decryptedBusinessNumber,
                decryptedBusinessName,
                decryptedBusinessAddress
        );
    }

    public SellerResponseDto getSellerByEmail(String email) {
        Seller seller = (Seller) sellerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCodes.SELLER_NOT_FOUND, "판매자를 찾을 수 없습니다."));

        // 복호화된 정보를 DTO로 반환
        String decryptedBusinessNumber = encryptionService.decrypt(seller.getEncryptedBusinessNumber());
        String decryptedBusinessName = encryptionService.decrypt(seller.getEncryptedBusinessName());
        String decryptedBusinessAddress = encryptionService.decrypt(seller.getEncryptedBusinessAddress());

        return new SellerResponseDto(
                seller.getEmail(),
                decryptedBusinessNumber,
                decryptedBusinessName,
                decryptedBusinessAddress
        );
    }

}
