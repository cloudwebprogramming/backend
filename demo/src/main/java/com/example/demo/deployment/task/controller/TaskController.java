package com.example.demo.deployment.task.controller;

import com.example.demo.deployment.task.dto.TaskAssigneeRequestDto;
import com.example.demo.deployment.task.dto.TaskCreateRequestDto;
import com.example.demo.deployment.task.dto.TaskResponseDto;
import com.example.demo.deployment.task.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class TaskController {
    private final TaskService taskService;
    private final com.example.demo.deployment.task.external.ProjectServiceClient projectServiceClient;

    public TaskController(TaskService taskService, com.example.demo.deployment.task.external.ProjectServiceClient projectServiceClient) {
        this.taskService = taskService;
        this.projectServiceClient = projectServiceClient;
    }

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponseDto> createTask(
            @PathVariable("projectId") Long projectId,
            @RequestBody TaskCreateRequestDto request) {
        TaskResponseDto response = taskService.createTask(projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/tasks/{taskId}/assignee")
    public ResponseEntity<TaskResponseDto> updateTaskAssignee(
            @PathVariable("taskId") Long taskId,
            @RequestBody TaskAssigneeRequestDto request) {
        TaskResponseDto response = taskService.updateTaskAssignee(taskId, request.getAssignee());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskResponseDto>> getTasksByProjectId(
            @PathVariable("projectId") Long projectId,
            @RequestParam(value = "assignee", required = false) String assignee,
            @RequestParam(value = "dueDate", required = false) LocalDate dueDate,
            @RequestParam(value = "priority", required = false) String priority,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "completed", required = false) Boolean completed,
            @RequestParam(value = "sortBy", required = false, defaultValue = "dueDate") String sortBy,
            @RequestParam(value = "sortOrder", required = false, defaultValue = "asc") String sortOrder) {
        List<TaskResponseDto> response = taskService.getTasksFiltered(
                projectId, assignee, dueDate, priority, category, completed, sortBy, sortOrder
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/projects/{projectId}/progress")
    public ResponseEntity<com.example.demo.deployment.task.dto.ProjectProgressDto> getProgress(
            @PathVariable("projectId") Long projectId) {
        com.example.demo.deployment.task.dto.ProjectProgressDto progress = taskService.getProgress(projectId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/tasks/urgent")
    public ResponseEntity<List<TaskResponseDto>> getUrgentTasks() {
        List<TaskResponseDto> response = taskService.getUrgentTasks();
        return ResponseEntity.ok(response);
    }

    // 2라운드 협업 및 동적 드롭박스 로딩을 위해 추가 노출하는 Mock API
    @GetMapping("/projects")
    public ResponseEntity<List<com.example.demo.deployment.task.dto.ProjectMockDto>> getProjects() {
        return ResponseEntity.ok(projectServiceClient.getProjects());
    }

    @GetMapping("/projects/{projectId}/members")
    public ResponseEntity<List<String>> getMembers(
            @PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok(projectServiceClient.getMembers(projectId));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
