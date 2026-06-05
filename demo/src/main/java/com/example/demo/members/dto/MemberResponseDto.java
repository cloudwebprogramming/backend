package com.example.demo.members.dto;

public class MemberResponseDto {
    private Long id;
    private Long projectId;
    private Long userId;
    private String username;
    private String name;
    private String status;

    public MemberResponseDto() {}

    public MemberResponseDto(Long id, Long projectId, Long userId, String username, String name, String status) {
        this.id = id;
        this.projectId = projectId;
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
