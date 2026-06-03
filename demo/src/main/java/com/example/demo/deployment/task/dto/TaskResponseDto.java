package com.example.demo.deployment.task.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskResponseDto {
    private Long taskId;
    private Long projectId;
    private String title;
    private String assignee;
    private String category;
    private String priority;
    private LocalDate dueDate;
    private String dday;
    private Boolean completed;

    public TaskResponseDto() {
    }

    public TaskResponseDto(Long taskId, Long projectId, String title, String assignee, 
                            String category, String priority, LocalDate dueDate, String dday, Boolean completed) {
        this.taskId = taskId;
        this.projectId = projectId;
        this.title = title;
        this.assignee = assignee;
        this.category = category;
        this.priority = priority;
        this.dueDate = dueDate;
        this.dday = dday;
        this.completed = completed;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getDday() {
        return dday;
    }

    public void setDday(String dday) {
        this.dday = dday;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
