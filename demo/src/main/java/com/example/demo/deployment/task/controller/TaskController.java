package com.example.demo.deployment.task.controller;

import com.example.demo.deployment.task.dto.TaskAssigneeRequestDto;
import com.example.demo.deployment.task.dto.TaskCreateRequestDto;
import com.example.demo.deployment.task.dto.TaskDetailUpdateRequestDto;
import com.example.demo.deployment.task.dto.TaskResponseDto;
import com.example.demo.deployment.task.dto.TaskStatusRequestDto;
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
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
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

    @PutMapping("/tasks/{taskId}/details")
    public ResponseEntity<TaskResponseDto> updateTaskDetails(
            @PathVariable("taskId") Long taskId,
            @RequestBody TaskDetailUpdateRequestDto request) {
        TaskResponseDto response = taskService.updateTaskDetails(taskId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/tasks/{taskId}/status")
    public ResponseEntity<TaskResponseDto> updateTaskStatus(
            @PathVariable("taskId") Long taskId,
            @RequestBody TaskStatusRequestDto request) {
        TaskResponseDto response = taskService.updateTaskStatus(taskId, request);
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


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
