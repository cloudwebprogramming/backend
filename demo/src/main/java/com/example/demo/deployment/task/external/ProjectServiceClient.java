package com.example.demo.deployment.task.external;

import com.example.demo.deployment.task.dto.ProjectMockDto;
import java.util.List;

public interface ProjectServiceClient {
    boolean validateProjectId(Long projectId);
    List<ProjectMockDto> getProjects();
    List<String> getMembers(Long projectId);
}
