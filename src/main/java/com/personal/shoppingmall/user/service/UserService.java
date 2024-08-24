package com.personal.shoppingmall.user.service;

import com.personal.shoppingmall.exception.CustomException;
import com.personal.shoppingmall.exception.ErrorCodes;
import com.personal.shoppingmall.security.util.EncryptionService;
import com.personal.shoppingmall.user.dto.SignupRequest;
import com.personal.shoppingmall.user.dto.SignupResponse;
import com.personal.shoppingmall.user.entity.User;
import com.personal.shoppingmall.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       EncryptionService encryptionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.encryptionService = encryptionService;
    }

    public SignupResponse signupUser(SignupRequest request) {
        logger.info("회원가입 요청 수신: {}", request);

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
        boolean isPasswordValid = isPasswordValid(request.getPassword());
        logger.info("비밀번호 검증 결과: {}", isPasswordValid);
        if (!isPasswordValid) {
            logger.warn("비밀번호 검증 실패: 비밀번호는 대문자, 소문자, 숫자 및 특수문자를 포함해야 합니다.");
            throw new CustomException(ErrorCodes.PASSWORD_VALIDATION_FAILED, "비밀번호는 대문자, 소문자, 숫자 및 특수문자를 포함해야 합니다.");
        }

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

        logger.info("사용자 저장 성공: {}", user.getEmail());

        return new SignupResponse("회원가입 성공.");
    }

    private boolean isPasswordValid(String password) {
        // 비밀번호 검증을 위한 정규 표현식
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        boolean isValid = Pattern.matches(passwordPattern, password);
        logger.info("비밀번호 검증 패턴: {}", passwordPattern);
        logger.info("비밀번호 검증: {} -> {}", password, isValid);
        return isValid;
    }
}
