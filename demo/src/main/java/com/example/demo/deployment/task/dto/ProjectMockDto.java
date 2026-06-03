package com.example.demo.deployment.task.dto;

public class ProjectMockDto {
    private Long id;
    private String title;
    private String subject;
    private String description;

    public ProjectMockDto() {
    }

    public ProjectMockDto(Long id, String title, String subject, String description) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
