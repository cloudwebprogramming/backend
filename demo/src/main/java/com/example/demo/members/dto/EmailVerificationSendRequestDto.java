package com.example.demo.members.dto;

public class EmailVerificationSendRequestDto {
    private String email;

    public EmailVerificationSendRequestDto() {}

    public EmailVerificationSendRequestDto(String email) {
        this.email = email;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
