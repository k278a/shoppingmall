package com.personal.shoppingmall.user.service;

import com.personal.shoppingmall.email.entity.UserEmailVerificationToken;
import com.personal.shoppingmall.email.repository.UserEmailVerificationTokenRepository;
import com.personal.shoppingmall.email.service.EmailService;
import com.personal.shoppingmall.email.service.EmailVerificationService;
import com.personal.shoppingmall.exception.CustomException;
import com.personal.shoppingmall.exception.ErrorCodes;
import com.personal.shoppingmall.security.jwt.JwtTokenProvider;
import com.personal.shoppingmall.security.entity.RoleName;
import com.personal.shoppingmall.security.util.EncryptionService;
import com.personal.shoppingmall.user.dto.*;
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
    private final UserEmailVerificationTokenRepository userTokenRepository;


    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       EncryptionService encryptionService,
                       EmailService emailService,
                       EmailVerificationService emailVerificationService,
                       JwtTokenProvider jwtTokenProvider,
                       UserEmailVerificationTokenRepository userTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.encryptionService = encryptionService;
        this.emailService = emailService;
        this.emailVerificationService = emailVerificationService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userTokenRepository = userTokenRepository;
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

        // 사용자 생성 및 저장 (기본 역할은 USER로 설정)
        User user = new User(
                request.getName(),
                request.getEmail(),
                encryptedPassword,
                encryptedPhoneNumber,
                encryptedAddress,
                RoleName.USER // 기본 역할 설정
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
        System.out.println("Token received: " + token);
        Optional<UserEmailVerificationToken> userTokenOpt = userTokenRepository.findByToken(token);
        if (userTokenOpt.isPresent()) {
            UserEmailVerificationToken userToken = userTokenOpt.get();
            System.out.println("Token found: " + userToken.getToken());
            if (userToken.isExpired()) {
                System.out.println("Token expired");
                throw new CustomException(ErrorCodes.INVALID_TOKEN, "유효하지 않거나 만료된 토큰입니다.");
            }
            User user = userToken.getUser();
            if (user != null) {
                user.updateVerifiedStatus(true); // 사용자 인증 상태 업데이트
                userRepository.save(user); // 사용자 저장
            }
            return "이메일 인증이 완료되었습니다.";
        }
        System.out.println("Token not found");
        throw new CustomException(ErrorCodes.INVALID_TOKEN, "유효하지 않거나 만료된 토큰입니다.");
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

        String jwtToken = jwtTokenProvider.createToken(user.getEmail(), user.getRole().getAuthority());
        logger.info("로그인 성공: {} - JWT 토큰 생성", user.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);

        return new ResponseEntity<>(new LoginResponse("로그인 성공"), headers, HttpStatus.OK);
    }

    public UserUpdateResponseDto updateUser(String token, UserUpdateRequestDto request) {
        // 토큰에서 이메일 추출
        String email = jwtTokenProvider.getUsernameFromToken(token);

        // 유저를 DB에서 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCodes.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 암호화할 필드 업데이트
        String encryptedPhoneNumber = encryptionService.encrypt(request.getPhoneNumber());
        String encryptedAddress = encryptionService.encrypt(request.getAddress());

        // 유저의 프로필 정보 업데이트
        user.update(request.getName(), encryptedPhoneNumber, encryptedAddress);

        // 변경 사항 저장
        userRepository.save(user);

        // 응답 생성 및 반환
        return new UserUpdateResponseDto("Profile updated successfully");
    }
    public UserProfileResponseDto getUserProfile(String token) {
        // 토큰에서 이메일 추출
        String email = jwtTokenProvider.getUsernameFromToken(token);

        // 유저를 DB에서 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCodes.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 사용자 정보를 담은 DTO 반환
        return new UserProfileResponseDto(
                user.getName(),
                user.getEmail(),
                encryptionService.decrypt(user.getPhoneNumber()),  // 암호화된 정보를 복호화
                encryptionService.decrypt(user.getAddress())     // 암호화된 정보를 복호화
        );
    }
}
