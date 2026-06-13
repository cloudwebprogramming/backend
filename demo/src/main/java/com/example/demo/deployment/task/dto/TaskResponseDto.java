package com.example.demo.deployment.task.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskResponseDto {
    private Long taskId;
    private Long projectId;
    private String title;
    private String description;
    private String detailNotes;
    private List<String> checklist = new ArrayList<>();
    private String assignee;
    private String category;
    private String priority;
    private String status;
    private LocalDate dueDate;
    private String dday;
    private Boolean completed;

    public TaskResponseDto() {
    }

    public TaskResponseDto(Long taskId, Long projectId, String title, String description, String detailNotes,
                            List<String> checklist, String assignee, String category, String priority, String status,
                            LocalDate dueDate, String dday, Boolean completed) {
        this.taskId = taskId;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.detailNotes = detailNotes;
        if (checklist != null) {
            this.checklist = checklist;
        }
        this.assignee = assignee;
        this.category = category;
        this.priority = priority;
        this.status = status;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetailNotes() {
        return detailNotes;
    }

    public void setDetailNotes(String detailNotes) {
        this.detailNotes = detailNotes;
    }

    public List<String> getChecklist() {
        return checklist;
    }

    public void setChecklist(List<String> checklist) {
        this.checklist = checklist;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
