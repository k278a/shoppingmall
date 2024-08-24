package com.personal.shoppingmall.email.service;

import com.personal.shoppingmall.email.entity.UserEmailVerificationToken;
import com.personal.shoppingmall.email.repository.UserEmailVerificationTokenRepository;
import com.personal.shoppingmall.exception.CustomException;
import com.personal.shoppingmall.exception.ErrorCodes;
import com.personal.shoppingmall.user.entity.User;
import com.personal.shoppingmall.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailVerificationService {

    @Value("${email.verification.token.expiry}")
    private long tokenExpiry;

    private final UserEmailVerificationTokenRepository userTokenRepository;
    private final UserRepository userRepository;

    public EmailVerificationService(UserEmailVerificationTokenRepository userTokenRepository, UserRepository userRepository) {
        this.userTokenRepository = userTokenRepository;
        this.userRepository = userRepository;
    }

    public String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        UserEmailVerificationToken verificationToken = new UserEmailVerificationToken(
                token, user, LocalDateTime.now().plusMinutes(tokenExpiry));
        userTokenRepository.save(verificationToken);

        return token;
    }

    public String verifyEmail(String token) {
        Optional<UserEmailVerificationToken> userTokenOpt = userTokenRepository.findByToken(token);
        if (userTokenOpt.isPresent()) {
            UserEmailVerificationToken userToken = userTokenOpt.get();
            if (userToken.isExpired()) {
                throw new CustomException(ErrorCodes.INVALID_TOKEN, "유효하지 않거나 만료된 토큰입니다.");
            }
            User user = userToken.getUser();
            if (user != null) {
                user.updateVerifiedStatus(true);
                userRepository.save(user);
            }
            return "이메일 인증이 완료되었습니다.";
        }
        throw new CustomException(ErrorCodes.INVALID_TOKEN, "유효하지 않거나 만료된 토큰입니다.");
    }


}
