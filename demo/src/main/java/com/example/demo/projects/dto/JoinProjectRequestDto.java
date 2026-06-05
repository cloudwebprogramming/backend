package com.example.demo.projects.dto;

public class JoinProjectRequestDto {
    private String inviteCode;
    private String username;

    public JoinProjectRequestDto() {}

    public JoinProjectRequestDto(String inviteCode, String username) {
        this.inviteCode = inviteCode;
        this.username = username;
    }

    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
