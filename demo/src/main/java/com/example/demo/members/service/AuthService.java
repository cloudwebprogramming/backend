package com.example.demo.members.service;

import com.example.demo.members.domain.User;
import com.example.demo.members.dto.SignUpRequestDto;
import com.example.demo.members.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtProvider jwtProvider;
    
    /**
     * Sign up
     */
    public User signUp(SignUpRequestDto requestDto) {
        // Validate input
        if (requestDto.getName() == null || requestDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("이름을 입력해주세요.");
        }
        if (requestDto.getStudentId() == null || requestDto.getStudentId().trim().isEmpty()) {
            throw new IllegalArgumentException("학번을 입력해주세요.");
        }
        if (requestDto.getEmail() == null || requestDto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("이메일을 입력해주세요.");
        }
        if (requestDto.getPassword() == null || requestDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }
        if (requestDto.getPasswordConfirm() == null || !requestDto.getPasswordConfirm().equals(requestDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        
        // Validate school email domain
        if (!emailService.isValidSchoolEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("학교 이메일로만 가입 가능합니다.");
        }

        String email = requestDto.getEmail().trim();
        String schoolId = extractSchoolId(email);
        
        // Check if email already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        
        // Check if student ID already exists
        if (userRepository.findByStudentId(requestDto.getStudentId()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 학번입니다.");
        }
        
        // Check if school ID already exists
        Optional<User> existingSchoolId = userRepository.findAll().stream()
                .filter(u -> u.getSchoolId() != null && u.getSchoolId().equalsIgnoreCase(schoolId))
                .findFirst();
        if (existingSchoolId.isPresent()) {
            throw new IllegalArgumentException("이미 가입된 학교 이메일 아이디입니다.");
        }

        if (!emailService.isEmailVerified(email)) {
            throw new IllegalArgumentException("학교 이메일 인증을 완료해주세요.");
        }
        
        // Create user
        User user = new User();
        user.setName(requestDto.getName());
        user.setSchoolId(schoolId);
        user.setStudentId(requestDto.getStudentId());
        user.setEmail(email);
        user.setUsername(schoolId);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setEmailVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        emailService.clearVerification(email);
        return savedUser;
    }

    private String extractSchoolId(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }
        return email.substring(0, atIndex);
    }
    
    /**
     * Verify email
     */
    public User verifyEmail(String email, String verificationCode) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        
        User user = optionalUser.get();
        
        // Check if code matches
        if (!verificationCode.equals(user.getVerificationCode())) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }
        
        // Check if code expired
        if (LocalDateTime.now().isAfter(user.getVerificationCodeExpiry())) {
            throw new IllegalArgumentException("인증 코드가 만료되었습니다.");
        }
        
        // Verify email
        user.setEmailVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    /**
     * Resend verification code
     */
    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            emailService.sendVerificationCode(email);
            return;
        }
        
        User user = optionalUser.get();
        
        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("이미 인증된 이메일입니다.");
        }
        
        // Generate new verification code
        String newCode = emailService.generateVerificationCode();
        user.setVerificationCode(newCode);
        user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(10));
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        
        // Send email
        if (!emailService.sendVerificationEmail(email, newCode)) {
            throw new RuntimeException("인증 코드 전송에 실패했습니다.");
        }
    }
    
    /**
     * Login
     */
    public String login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
        
        User user = optionalUser.get();
        
        // Check if email is verified
        if (!user.isEmailVerified()) {
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
        }
        
        // Check password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
        
        // Generate JWT token
        return jwtProvider.generateToken(user.getId(), user.getEmail());
    }
    
    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
