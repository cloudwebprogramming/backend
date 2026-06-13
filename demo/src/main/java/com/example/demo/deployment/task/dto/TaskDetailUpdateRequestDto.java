package com.example.demo.deployment.task.dto;

import java.util.ArrayList;
import java.util.List;

public class TaskDetailUpdateRequestDto {
    private String detailNotes;
    private List<String> checklist = new ArrayList<>();

    public TaskDetailUpdateRequestDto() {
    }

    public TaskDetailUpdateRequestDto(String detailNotes, List<String> checklist) {
        this.detailNotes = detailNotes;
        if (checklist != null) {
            this.checklist = checklist;
        }
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
}
