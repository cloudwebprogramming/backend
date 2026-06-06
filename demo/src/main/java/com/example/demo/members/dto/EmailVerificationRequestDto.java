package com.example.demo.members.dto;

public class EmailVerificationRequestDto {
    private String email;
    private String verificationCode;

    public EmailVerificationRequestDto() {}

    public EmailVerificationRequestDto(String email, String verificationCode) {
        this.email = email;
        this.verificationCode = verificationCode;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }
}
