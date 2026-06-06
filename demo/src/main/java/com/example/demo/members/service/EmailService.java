package com.example.demo.members.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String senderEmail;
    
    @Value("${school.email.domain}")
    private String schoolEmailDomain;

    private final Map<String, EmailVerification> verificationStore = new ConcurrentHashMap<>();
    
    /**
     * Generate verification code
     */
    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6-digit code
        return String.valueOf(code);
    }
    
    /**
     * Send verification email
     */
    public boolean sendVerificationEmail(String recipientEmail, String verificationCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(recipientEmail);
            message.setSubject("[학교 시스템] 이메일 인증 코드");
            message.setText("안녕하세요.\n\n"
                    + "다음은 이메일 인증 코드입니다.\n\n"
                    + "인증 코드: " + verificationCode + "\n\n"
                    + "10분 이내에 인증해주세요.\n\n"
                    + "감사합니다.");
            
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void sendVerificationCode(String email) {
        if (!isValidSchoolEmail(email)) {
            throw new IllegalArgumentException("학교 이메일로만 가입 가능합니다.");
        }

        String normalizedEmail = normalizeEmail(email);
        String verificationCode = generateVerificationCode();
        verificationStore.put(normalizedEmail, new EmailVerification(verificationCode, LocalDateTime.now().plusMinutes(10), false));

        if (!sendVerificationEmail(normalizedEmail, verificationCode)) {
            verificationStore.remove(normalizedEmail);
            throw new RuntimeException("인증 코드 전송에 실패했습니다.");
        }
    }

    public void verifyCode(String email, String verificationCode) {
        String normalizedEmail = normalizeEmail(email);
        EmailVerification verification = verificationStore.get(normalizedEmail);

        if (verification == null) {
            throw new IllegalArgumentException("전송된 인증 코드가 없습니다.");
        }
        if (LocalDateTime.now().isAfter(verification.expiry())) {
            verificationStore.remove(normalizedEmail);
            throw new IllegalArgumentException("인증 코드가 만료되었습니다.");
        }
        if (!verification.code().equals(verificationCode)) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }

        verificationStore.put(normalizedEmail, new EmailVerification(verification.code(), verification.expiry(), true));
    }

    public boolean isEmailVerified(String email) {
        EmailVerification verification = verificationStore.get(normalizeEmail(email));
        return verification != null
                && verification.verified()
                && LocalDateTime.now().isBefore(verification.expiry());
    }

    public void clearVerification(String email) {
        verificationStore.remove(normalizeEmail(email));
    }
    
    /**
     * Validate school email domain
     */
    public boolean isValidSchoolEmail(String email) {
        return email != null && normalizeEmail(email).endsWith(schoolEmailDomain.toLowerCase());
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private record EmailVerification(String code, LocalDateTime expiry, boolean verified) {}
}
