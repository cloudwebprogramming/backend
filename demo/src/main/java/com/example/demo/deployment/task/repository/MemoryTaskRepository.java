package com.example.demo.deployment.task.repository;

import com.example.demo.deployment.task.domain.Task;
import org.springframework.stereotype.Repository;
import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class MemoryTaskRepository implements TaskRepository {
    private final List<Task> database = new CopyOnWriteArrayList<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    @PostConstruct
    public void initMockData() {
        // 테스트 및 데모 편의성을 위해 초기 Mock 데이터 삽입
        save(new Task(null, 1L, "발표 PPT 템플릿 제작", "발표 과제용 PPT 디자인 초안 구성", "홍길동", "발표자료", "높음", LocalDate.now().plusDays(3)));
        save(new Task(null, 1L, "백엔드 API 아키텍처 구축", "역할 격리 패키지 및 DTO 스펙 규격화", "김철수", "백엔드", "높음", LocalDate.now().plusDays(5)));
        save(new Task(null, 2L, "요구사항 분석 보고서 작성", "과제 기획 분석 보고서 작성", "이영희", "보고서", "보통", LocalDate.now().minusDays(1)));
    }

    @Override
    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(idSequence.getAndIncrement());
        } else {
            // 기존 데이터가 있으면 삭제 후 갱신
            findById(task.getId()).ifPresent(database::remove);
        }
        database.add(task);
        return task;
    }

    @Override
    public Optional<Task> findById(Long id) {
        return database.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(database);
    }

    @Override
    public List<Task> findByProjectId(Long projectId) {
        return database.stream()
                .filter(task -> task.getProjectId().equals(projectId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByProjectIdAndAssignee(Long projectId, String assignee) {
        return database.stream()
                .filter(task -> task.getProjectId().equals(projectId))
                .filter(task -> task.getAssignee() != null && task.getAssignee().equals(assignee))
                .collect(Collectors.toList());
    }

    @Override
    public void clear() {
        database.clear();
        idSequence.set(1);
    }
}
