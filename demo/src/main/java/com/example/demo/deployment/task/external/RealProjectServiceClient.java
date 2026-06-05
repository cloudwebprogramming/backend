package com.example.demo.deployment.task.external;

import com.example.demo.deployment.task.dto.ProjectMockDto;
import com.example.demo.projects.service.ProjectService;
import com.example.demo.projects.dto.ProjectResponseDto;
import com.example.demo.members.service.MemberService;
import com.example.demo.members.dto.MemberResponseDto;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
public class RealProjectServiceClient implements ProjectServiceClient {
    
    private final ProjectService projectService;
    private final MemberService memberService;

    public RealProjectServiceClient(ProjectService projectService, MemberService memberService) {
        this.projectService = projectService;
        this.memberService = memberService;
    }

    @Override
    public boolean validateProjectId(Long projectId) {
        try {
            projectService.getProject(projectId);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public List<ProjectMockDto> getProjects() {
        // Return all projects mapped to ProjectMockDto
        return projectService.getAllProjects().stream()
                .map(p -> new ProjectMockDto(p.getId(), p.getTitle(), p.getSubject(), p.getDescription()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getMembers(Long projectId) {
        return memberService.getAcceptedMembers(projectId).stream()
                .map(MemberResponseDto::getName)
                .collect(Collectors.toList());
    }
}
