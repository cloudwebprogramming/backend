package com.example.demo.deployment.task.dto;

public class ProjectProgressDto {
    private Long projectId;
    private double progress;
    private int totalTasks;
    private int completedTasks;

    public ProjectProgressDto() {
    }

    public ProjectProgressDto(Long projectId, double progress, int totalTasks, int completedTasks) {
        this.projectId = projectId;
        this.progress = progress;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }
}
