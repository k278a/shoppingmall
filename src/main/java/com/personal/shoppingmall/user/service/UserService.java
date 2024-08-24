package com.personal.shoppingmall.user.service;

import com.personal.shoppingmall.email.service.EmailService;
import com.personal.shoppingmall.email.service.EmailVerificationService;
import com.personal.shoppingmall.exception.CustomException;
import com.personal.shoppingmall.exception.ErrorCodes;
import com.personal.shoppingmall.security.jwt.JwtTokenProvider;
import com.personal.shoppingmall.security.util.EncryptionService;
import com.personal.shoppingmall.user.dto.LoginRequest;
import com.personal.shoppingmall.user.dto.LoginResponse;
import com.personal.shoppingmall.user.dto.SignupRequest;
import com.personal.shoppingmall.user.dto.SignupResponse;
import com.personal.shoppingmall.user.entity.User;
import com.personal.shoppingmall.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       EncryptionService encryptionService,
                       EmailService emailService,
                       EmailVerificationService emailVerificationService,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.encryptionService = encryptionService;
        this.emailService = emailService;
        this.emailVerificationService = emailVerificationService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public SignupResponse signupUser(SignupRequest request) {
        logger.info("회원가입 요청 수신: {}", request);

        // 이메일 형식 검증
        if (!isEmailValid(request.getEmail())) {
            logger.warn("이메일 형식 오류: {}", request.getEmail());
            throw new CustomException(ErrorCodes.INVALID_EMAIL_FORMAT, "유효하지 않은 이메일 형식입니다.");
        }

        // 이메일 중복 확인
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            logger.warn("이메일 중복: {}", request.getEmail());
            throw new CustomException(ErrorCodes.USER_ALREADY_EXISTS, "이미 등록된 이메일입니다.");
        }
        logger.info("이메일 중복 확인 통과: {}", request.getEmail());

        // 비밀번호 일치 확인
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            logger.warn("비밀번호 불일치: 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
            throw new CustomException(ErrorCodes.PASSWORD_MISMATCH, "비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }
        logger.info("비밀번호 일치 확인 통과");

        // 비밀번호 검증
        if (!isPasswordValid(request.getPassword())) {
            logger.warn("비밀번호 검증 실패: 비밀번호는 대문자, 소문자, 숫자 및 특수문자를 포함해야 합니다.");
            throw new CustomException(ErrorCodes.PASSWORD_VALIDATION_FAILED, "비밀번호는 대문자, 소문자, 숫자 및 특수문자를 포함해야 합니다.");
        }
        logger.info("비밀번호 검증 통과");

        // 전화번호 형식 검증
        if (!isPhoneNumberValid(request.getPhoneNumber())) {
            logger.warn("전화번호 형식 오류: {}", request.getPhoneNumber());
            throw new CustomException(ErrorCodes.INVALID_PHONE_NUMBER_FORMAT, "유효하지 않은 전화번호 형식입니다.");
        }
        logger.info("전화번호 형식 확인 통과: {}", request.getPhoneNumber());

        // 비밀번호 및 기타 필드 암호화
        String encryptedPassword = passwordEncoder.encode(request.getPassword());
        String encryptedPhoneNumber = encryptionService.encrypt(request.getPhoneNumber());
        String encryptedAddress = encryptionService.encrypt(request.getAddress());

        logger.info("비밀번호 암호화: {}", encryptedPassword);
        logger.info("전화번호 암호화: {}", encryptedPhoneNumber);
        logger.info("주소 암호화: {}", encryptedAddress);

        // 사용자 생성 및 저장
        User user = new User(
                request.getName(),
                request.getEmail(),
                encryptedPassword,
                encryptedPhoneNumber,
                encryptedAddress
        );
        userRepository.save(user);

        // 이메일 인증 토큰 생성
        String token = emailVerificationService.generateVerificationToken(user);

        // 이메일 발송
        emailService.sendVerificationEmail(user, token);

        logger.info("이메일 인증 토큰 생성 및 발송 완료: {}", token);

        return new SignupResponse("회원가입 성공. 인증 이메일을 확인하세요.");
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

    public ResponseEntity<LoginResponse> loginUser(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            logger.warn("사용자 찾을 수 없음: {}", request.getEmail());
            throw new CustomException(ErrorCodes.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        User user = userOpt.get();

        // 이메일 인증 여부 확인
        if (!user.isVerified()) {
            logger.warn("이메일 인증되지 않음: {}", request.getEmail());
            throw new CustomException(ErrorCodes.EMAIL_NOT_VERIFIED, "이메일 인증이 필요합니다. 이메일을 확인하세요.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("비밀번호 불일치: {}", request.getEmail());
            throw new CustomException(ErrorCodes.INVALID_PASSWORD, "비밀번호가 잘못되었습니다.");
        }

        String jwtToken = jwtTokenProvider.createToken(user.getEmail());
        logger.info("로그인 성공: {} - JWT 토큰 생성", user.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);

        return new ResponseEntity<>(new LoginResponse("로그인 성공"), headers, HttpStatus.OK);
    }
}
