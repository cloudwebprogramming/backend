package com.example.demo.deployment.task.dto;

public class TaskStatusRequestDto {
    private String status;

    public TaskStatusRequestDto() {
    }

    public TaskStatusRequestDto(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
