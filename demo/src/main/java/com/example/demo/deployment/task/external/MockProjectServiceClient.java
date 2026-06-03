package com.example.demo.deployment.task.external;

import com.example.demo.deployment.task.dto.ProjectMockDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class MockProjectServiceClient implements ProjectServiceClient {
    
    private final List<ProjectMockDto> mockProjects = new ArrayList<>();

    public MockProjectServiceClient() {
        mockProjects.add(new ProjectMockDto(1L, "클라우드기반웹개발 팀프로젝트", "클라우드웹개발", "Todo 기능 확장 프로젝트"));
        mockProjects.add(new ProjectMockDto(2L, "데이터베이스 발표 과제", "데이터베이스", "발표 및 기획 보고서 작성"));
    }

    @Override
    public boolean validateProjectId(Long projectId) {
        return projectId != null && mockProjects.stream().anyMatch(p -> p.getId().equals(projectId));
    }

    @Override
    public List<ProjectMockDto> getProjects() {
        return new ArrayList<>(mockProjects);
    }

    @Override
    public List<String> getMembers(Long projectId) {
        if (projectId == null) {
            return Collections.emptyList();
        }
        if (projectId.equals(1L)) {
            return Arrays.asList("홍길동", "김철수");
        } else if (projectId.equals(2L)) {
            return Arrays.asList("이영희", "박민수");
        }
        return Collections.emptyList();
    }
}
