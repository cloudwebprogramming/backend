package com.example.demo.members.dto;

import com.example.demo.members.domain.User;

public class UserAuthResponseDto {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String schoolId;
    private String studentId;
    private boolean emailVerified;

    public UserAuthResponseDto() {}

    public UserAuthResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.email = user.getEmail();
        this.schoolId = user.getSchoolId();
        this.studentId = user.getStudentId();
        this.emailVerified = user.isEmailVerified();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
}
