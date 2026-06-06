package com.example.demo.members.dto;

public class SignUpRequestDto {
    private String name;
    private String schoolId;
    private String email;
    private String password;
    private String passwordConfirm;
    private String studentId;

    public SignUpRequestDto() {}

    public SignUpRequestDto(String name, String schoolId, String email, String password, String passwordConfirm, String studentId) {
        this.name = name;
        this.schoolId = schoolId;
        this.email = email;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.studentId = studentId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPasswordConfirm() { return passwordConfirm; }
    public void setPasswordConfirm(String passwordConfirm) { this.passwordConfirm = passwordConfirm; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
}
