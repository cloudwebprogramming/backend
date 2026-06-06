package com.example.demo.members.controller;

import com.example.demo.members.domain.User;
import com.example.demo.members.dto.*;
import com.example.demo.members.service.AuthService;
import com.example.demo.members.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")
public class AuthController {
    
    private final AuthService authService;
    private final EmailService emailService;
    
    public AuthController(AuthService authService, EmailService emailService) {
        this.authService = authService;
        this.emailService = emailService;
    }
    
    /**
     * Sign up
     * POST /api/v1/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequestDto requestDto) {
        try {
            User user = authService.signUp(requestDto);

            AuthResponseDto response = new AuthResponseDto(
                    true,
                    "회원가입이 완료되었습니다.",
                    new UserAuthResponseDto(user)
            );
            return ResponseEntity.status(201).body(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponseDto(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new AuthResponseDto(false, "회원가입 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * Send verification email
     * POST /api/v1/auth/send-verification
     */
    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerificationCode(@RequestBody EmailVerificationSendRequestDto requestDto) {
        try {
            authService.resendVerificationCode(requestDto.getEmail());
            return ResponseEntity.ok(
                    new AuthResponseDto(true, "인증 코드가 이메일로 전송되었습니다.")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponseDto(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new AuthResponseDto(false, "인증 코드 전송 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * Verify email
     * POST /api/v1/auth/verify-email
     */
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody EmailVerificationRequestDto requestDto) {
        try {
            emailService.verifyCode(requestDto.getEmail(), requestDto.getVerificationCode());
            return ResponseEntity.ok(
                    new AuthResponseDto(true, "이메일 인증이 완료되었습니다.")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponseDto(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new AuthResponseDto(false, "이메일 인증 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * Login
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto requestDto) {
        try {
            String token = authService.login(requestDto.getEmail(), requestDto.getPassword());
            User user = authService.getUserByEmail(requestDto.getEmail());
            
            AuthResponseDto response = new AuthResponseDto(
                    true,
                    "로그인되었습니다.",
                    new UserAuthResponseDto(user),
                    token
            );
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponseDto(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new AuthResponseDto(false, "로그인 중 오류가 발생했습니다."));
        }
    }
}
