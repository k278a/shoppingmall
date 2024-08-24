package com.personal.shoppingmall.seller.service;

import com.personal.shoppingmall.email.service.EmailService;
import com.personal.shoppingmall.email.service.EmailVerificationService;
import com.personal.shoppingmall.exception.CustomException;
import com.personal.shoppingmall.exception.ErrorCodes;
import com.personal.shoppingmall.security.jwt.JwtTokenProvider;
import com.personal.shoppingmall.security.util.EncryptionService;
import com.personal.shoppingmall.seller.dto.SellerLoginRequestDto;
import com.personal.shoppingmall.seller.dto.SellerLoginResponseDto;
import com.personal.shoppingmall.seller.dto.SellerSignupRequestDto;
import com.personal.shoppingmall.seller.dto.SellerSignupResponseDto;
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

        // Seller 객체 생성 및 저장
        Seller seller = Seller.builder()
                .email(request.getEmail())
                .encryptedPassword(encryptedPassword)
                .encryptedBusinessNumber(request.getBusinessNumber()) // 비즈니스 번호 암호화 로직 필요
                .encryptedBusinessName(request.getBusinessName()) // 비즈니스 이름 암호화 로직 필요
                .encryptedBusinessAddress(request.getBusinessAddress()) // 비즈니스 주소 암호화 로직 필요
                .build();

        sellerRepository.save(seller);

        // 이메일 인증 토큰 생성 및 발송
        String token = emailVerificationService.generateVerificationToken(seller);
        emailService.sendVerificationEmail(seller, token);

        logger.info("판매자 회원가입 성공: {} - 인증 이메일을 발송했습니다.", request.getEmail());

        return new SellerSignupResponseDto("회원가입 성공. 인증 이메일을 확인하세요.");
    }

    // 비밀번호 유효성 검사
    private boolean isValidPassword(String password) {
        // 비밀번호는 최소 8자 이상, 대문자, 소문자, 숫자, 특수문자를 포함해야 함
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return Pattern.compile(regex).matcher(password).matches();
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
}
