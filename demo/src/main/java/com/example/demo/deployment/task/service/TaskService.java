package com.example.demo.deployment.task.service;

import com.example.demo.deployment.task.domain.Task;
import com.example.demo.deployment.task.dto.TaskCreateRequestDto;
import com.example.demo.deployment.task.dto.TaskResponseDto;
import com.example.demo.deployment.task.external.ProjectServiceClient;
import com.example.demo.deployment.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectServiceClient projectServiceClient;

    public TaskService(TaskRepository taskRepository, ProjectServiceClient projectServiceClient) {
        this.taskRepository = taskRepository;
        this.projectServiceClient = projectServiceClient;
    }

    public TaskResponseDto createTask(Long projectId, TaskCreateRequestDto request) {
        // 1. 입력 유효성 검사
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("할 일 제목은 필수 입력 항목입니다.");
        }
        if (request.getDueDate() == null) {
            throw new IllegalArgumentException("마감일은 필수 입력 항목입니다.");
        }

        // 2. 외부 프로젝트 검증
        if (!projectServiceClient.validateProjectId(projectId)) {
            throw new IllegalArgumentException("유효하지 않은 프로젝트 ID 입니다.");
        }

        // 3. Task 도메인 객체 생성
        Task task = new Task(
                null,
                projectId,
                request.getTitle(),
                request.getDescription(),
                request.getAssignee(),
                request.getCategory(),
                request.getPriority(),
                request.getDueDate()
        );

        // 4. 저장
        Task savedTask = taskRepository.save(task);

        // 5. 응답 매핑
        return convertToResponseDto(savedTask);
    }

    public TaskResponseDto updateTaskAssignee(Long taskId, String assignee) {
        // 1. 할 일 존재 여부 검증
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 할 일을 찾을 수 없습니다. ID: " + taskId));

        // 2. 담당자 업데이트 (빈 값인 경우 null로 처리하여 지정 취소 지원)
        if (assignee == null || assignee.trim().isEmpty()) {
            task.setAssignee(null);
        } else {
            task.setAssignee(assignee);
        }

        task.setUpdatedAt(java.time.LocalDateTime.now());

        // 3. 저장 및 반환
        Task updatedTask = taskRepository.save(task);
        return convertToResponseDto(updatedTask);
    }

    public List<TaskResponseDto> getTasksByProjectId(Long projectId) {
        return getTasksFiltered(projectId, null, null, null, null, null, "dueDate", "asc");
    }

    public List<TaskResponseDto> getTasksFiltered(Long projectId, String assignee, LocalDate dueDate,
                                                  String priority, String category, Boolean completed,
                                                  String sortBy, String sortOrder) {
        // 1. 조건에 따른 할 일 로드 (분기 처리)
        List<Task> tasks;
        if (assignee != null && !assignee.trim().isEmpty()) {
            tasks = taskRepository.findByProjectIdAndAssignee(projectId, assignee);
        } else {
            tasks = taskRepository.findByProjectId(projectId);
        }

        // 2. 나머지 다중 조건 필터링 적용
        java.util.stream.Stream<Task> stream = tasks.stream();
        if (dueDate != null) {
            stream = stream.filter(t -> t.getDueDate() != null && t.getDueDate().equals(dueDate));
        }
        if (priority != null && !priority.trim().isEmpty()) {
            stream = stream.filter(t -> t.getPriority() != null && t.getPriority().equals(priority));
        }
        if (category != null && !category.trim().isEmpty()) {
            stream = stream.filter(t -> t.getCategory() != null && t.getCategory().equals(category));
        }
        if (completed != null) {
            stream = stream.filter(t -> t.getCompleted() != null && t.getCompleted().equals(completed));
        }

        // 3. 정렬 조건 적용 (3.8)
        java.util.Comparator<Task> comparator = getComparator(sortBy);
        if (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) {
            comparator = comparator.reversed();
        }

        return stream.sorted(comparator)
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public com.example.demo.deployment.task.dto.ProjectProgressDto getProgress(Long projectId) {
        if (!projectServiceClient.validateProjectId(projectId)) {
            throw new IllegalArgumentException("유효하지 않은 프로젝트 ID 입니다.");
        }
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        if (tasks.isEmpty()) {
            return new com.example.demo.deployment.task.dto.ProjectProgressDto(projectId, 0.0, 0, 0);
        }

        int total = tasks.size();
        long completed = tasks.stream().filter(Task::getCompleted).count();
        double progressRatio = Math.round(((double) completed / total * 100) * 10) / 10.0;

        return new com.example.demo.deployment.task.dto.ProjectProgressDto(projectId, progressRatio, total, (int) completed);
    }

    public List<TaskResponseDto> getUrgentTasks() {
        LocalDate today = LocalDate.now();
        return taskRepository.findAll().stream()
                .filter(t -> !t.getCompleted())
                .filter(t -> t.getDueDate() != null)
                .filter(t -> {
                    long diff = ChronoUnit.DAYS.between(today, t.getDueDate());
                    return diff >= 0 && diff <= 2; // 오늘 마감부터 D-2 마감까지
                })
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    private java.util.Comparator<Task> getComparator(String sortBy) {
        if (sortBy == null) {
            return java.util.Comparator.comparing(Task::getDueDate, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()));
        }
        switch (sortBy.toLowerCase()) {
            case "priority":
                return java.util.Comparator.comparingInt((Task t) -> getPriorityWeight(t.getPriority())); // 오름차순 (낮음 -> 높음)
            case "createdat":
                return java.util.Comparator.comparing(Task::getCreatedAt, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()));
            case "duedate":
            default:
                return java.util.Comparator.comparing(Task::getDueDate, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()));
        }
    }

    private int getPriorityWeight(String priority) {
        if (priority == null) return 0;
        switch (priority) {
            case "높음": return 3;
            case "보통": return 2;
            case "낮음": return 1;
            default: return 0;
        }
    }

    public String calculateDDay(LocalDate dueDate) {
        if (dueDate == null) {
            return "";
        }
        LocalDate today = LocalDate.now();
        long diff = ChronoUnit.DAYS.between(today, dueDate);

        if (diff == 0) {
            return "D-Day";
        } else if (diff > 0) {
            return "D-" + diff;
        } else {
            return "D+" + Math.abs(diff);
        }
    }

    private TaskResponseDto convertToResponseDto(Task task) {
        return new TaskResponseDto(
                task.getId(),
                task.getProjectId(),
                task.getTitle(),
                task.getAssignee(),
                task.getCategory(),
                task.getPriority(),
                task.getDueDate(),
                calculateDDay(task.getDueDate()),
                task.getCompleted()
        );
    }
}
