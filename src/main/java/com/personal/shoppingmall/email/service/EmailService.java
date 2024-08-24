package com.personal.shoppingmall.email.service;


import com.personal.shoppingmall.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class  EmailService {

    private final JavaMailSender mailSender;

    @Value("${email.verification.url}")
    private String verificationUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(User user, String token) {
        String subject = "Email Verification";
        String confirmationUrl = verificationUrl + "/" + token;

        String text = "Please verify your email by clicking the link below:\n" + confirmationUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
    
}
