package com.example.demo.deployment.task.repository;

import com.example.demo.deployment.task.domain.Task;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Task save(Task task);
    Optional<Task> findById(Long id);
    List<Task> findAll();
    List<Task> findByProjectId(Long projectId);
    void clear();
}
