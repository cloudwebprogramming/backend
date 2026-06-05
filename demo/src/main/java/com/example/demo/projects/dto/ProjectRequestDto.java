package com.example.demo.projects.dto;

public class ProjectRequestDto {
    private String title;
    private String subject;
    private String description;
    private String inviteCode;
    private String creatorUsername;

    public ProjectRequestDto() {}

    public ProjectRequestDto(String title, String subject, String description, String inviteCode, String creatorUsername) {
        this.title = title;
        this.subject = subject;
        this.description = description;
        this.inviteCode = inviteCode;
        this.creatorUsername = creatorUsername;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
    public String getCreatorUsername() { return creatorUsername; }
    public void setCreatorUsername(String creatorUsername) { this.creatorUsername = creatorUsername; }
}
