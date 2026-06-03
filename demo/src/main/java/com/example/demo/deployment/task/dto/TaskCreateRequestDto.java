package com.example.demo.deployment.task.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskCreateRequestDto {
    private String title;
    private String description;
    private String assignee;
    private String category;
    private String priority = "보통";
    private LocalDate dueDate;

    public TaskCreateRequestDto() {
    }

    public TaskCreateRequestDto(String title, String description, String assignee, 
                                String category, String priority, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.assignee = assignee;
        this.category = category;
        if (priority != null && !priority.trim().isEmpty()) {
            this.priority = priority;
        }
        this.dueDate = dueDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
