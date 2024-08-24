package com.personal.shoppingmall.email.service;

import com.personal.shoppingmall.email.entity.SellerEmailVerificationToken;
import com.personal.shoppingmall.email.entity.UserEmailVerificationToken;
import com.personal.shoppingmall.email.repository.SellerEmailVerificationTokenRepository;
import com.personal.shoppingmall.email.repository.UserEmailVerificationTokenRepository;
import com.personal.shoppingmall.exception.CustomException;
import com.personal.shoppingmall.exception.ErrorCodes;
import com.personal.shoppingmall.seller.entity.Seller;
import com.personal.shoppingmall.seller.repository.SellerRepository;
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
    private final SellerEmailVerificationTokenRepository sellerTokenRepository;
    private final SellerRepository sellerRepository;

    public EmailVerificationService(UserEmailVerificationTokenRepository userTokenRepository, UserRepository userRepository, SellerEmailVerificationTokenRepository sellerTokenRepository, SellerRepository sellerRepository) {
        this.userTokenRepository = userTokenRepository;
        this.userRepository = userRepository;
        this.sellerTokenRepository = sellerTokenRepository;
        this.sellerRepository = sellerRepository;
    }

    public String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        UserEmailVerificationToken verificationToken = new UserEmailVerificationToken(
                token, user, LocalDateTime.now().plusMinutes(tokenExpiry));
        userTokenRepository.save(verificationToken);
        return token;
    }

    public String generateVerificationToken(Seller seller) {
        String token = UUID.randomUUID().toString();
        SellerEmailVerificationToken verificationToken = new SellerEmailVerificationToken(
                token, seller, LocalDateTime.now().plusMinutes(tokenExpiry));
        sellerTokenRepository.save(verificationToken);
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

        Optional<SellerEmailVerificationToken> sellerTokenOpt = sellerTokenRepository.findByToken(token);
        if (sellerTokenOpt.isPresent()) {
            SellerEmailVerificationToken sellerToken = sellerTokenOpt.get();
            if (sellerToken.isExpired()) {
                throw new CustomException(ErrorCodes.INVALID_TOKEN, "유효하지 않거나 만료된 토큰입니다.");
            }
            Seller seller = sellerToken.getSeller();
            if (seller != null) {
                seller.updateVerifiedStatus(true);
                sellerRepository.save(seller);
            }
            return "이메일 인증이 완료되었습니다.";
        }
        throw new CustomException(ErrorCodes.INVALID_TOKEN, "유효하지 않거나 만료된 토큰입니다.");
    }
}
