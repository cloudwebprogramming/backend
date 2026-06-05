package com.example.demo.members.dto;

public class MemberRequestDto {
    private String username;
    private String inviteCode;

    public MemberRequestDto() {}

    public MemberRequestDto(String username, String inviteCode) {
        this.username = username;
        this.inviteCode = inviteCode;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
}
