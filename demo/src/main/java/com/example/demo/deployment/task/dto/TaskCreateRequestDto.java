package com.example.demo.deployment.task.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskCreateRequestDto {
    private String title;
    private String description;
    private String detailNotes;
    private List<String> checklist = new ArrayList<>();
    private String assignee;
    private String category;
    private String priority = "보통";
    private String status = "예정";
    private LocalDate dueDate;

    public TaskCreateRequestDto() {
    }

    public TaskCreateRequestDto(String title, String description, String assignee,
                                String category, String priority, LocalDate dueDate) {
        this(title, description, "", new ArrayList<>(), assignee, category, priority, dueDate);
    }

    public TaskCreateRequestDto(String title, String description, String detailNotes, List<String> checklist,
                                String assignee, String category, String priority, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.detailNotes = detailNotes;
        if (checklist != null) {
            this.checklist = checklist;
        }
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
        if (status != null && !status.trim().isEmpty()) {
            this.status = status;
        }
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
