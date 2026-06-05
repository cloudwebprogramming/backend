package com.example.demo.deployment.task.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskAssigneeRequestDto {
    private String assignee;

    public TaskAssigneeRequestDto() {
    }

    public TaskAssigneeRequestDto(String assignee) {
        this.assignee = assignee;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
}
