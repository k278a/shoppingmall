package com.personal.shoppingmall.email.service;


import com.personal.shoppingmall.seller.entity.Seller;
import com.personal.shoppingmall.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class  EmailService {

    private final JavaMailSender mailSender;

    @Value("${email.verification.url.user}")
    private String userVerificationUrl;

    @Value("${email.verification.url.seller}")
    private String sellerVerificationUrl;



    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(User user, String token) {
        String subject = "Email Verification";
        String confirmationUrl = userVerificationUrl + "/" + token;

        String text = "Please verify your email by clicking the link below:\n" + confirmationUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    public void sendVerificationEmail(Seller seller, String token) {
        String subject = "Email Verification";
        String confirmationUrl = sellerVerificationUrl + "/" + token;

        String text = "Please verify your email by clicking the link below:\n" + confirmationUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(seller.getEmail());
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
    
}
