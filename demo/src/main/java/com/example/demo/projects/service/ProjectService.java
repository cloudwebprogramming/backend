package com.example.demo.projects.service;

import com.example.demo.members.service.MemberService;
import com.example.demo.projects.domain.Project;
import com.example.demo.projects.dto.ProjectRequestDto;
import com.example.demo.projects.dto.ProjectResponseDto;
import com.example.demo.projects.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final MemberService memberService;

    public ProjectService(ProjectRepository projectRepository, MemberService memberService) {
        this.projectRepository = projectRepository;
        this.memberService = memberService;
    }

    public ProjectResponseDto createProject(ProjectRequestDto requestDto) {
        if (requestDto.getTitle() == null || requestDto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("프로젝트 제목은 필수입니다.");
        }
        
        String inviteCode = requestDto.getInviteCode();
        if (inviteCode == null || inviteCode.isEmpty()) {
            inviteCode = "INV-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } else {
            if (projectRepository.findByInviteCode(inviteCode).isPresent()) {
                throw new IllegalArgumentException("이미 사용 중인 초대 코드입니다.");
            }
        }

        Project project = new Project(null, requestDto.getTitle(), requestDto.getSubject(), requestDto.getDescription(), inviteCode);
        Project savedProject = projectRepository.save(project);

        if (requestDto.getCreatorUsername() != null && !requestDto.getCreatorUsername().isEmpty()) {
            memberService.addCreatorAsAcceptedMember(savedProject.getId(), requestDto.getCreatorUsername());
        }

        return getProject(savedProject.getId());
    }

    public ProjectResponseDto joinByInviteCode(String inviteCode, String username) {
        if (inviteCode == null || inviteCode.isEmpty()) {
            throw new IllegalArgumentException("초대 코드는 필수입니다.");
        }
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("사용자 아이디는 필수입니다.");
        }

        Project project = projectRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 코드입니다."));

        memberService.joinProject(project.getId(), username);

        return getProject(project.getId());
    }

    public List<ProjectResponseDto> getProjectsByUsername(String username) {
        return projectRepository.findAll().stream()
                .filter(p -> memberService.getAcceptedMembers(p.getId()).stream()
                        .anyMatch(m -> m.getUsername().equals(username)))
                .map(p -> new ProjectResponseDto(
                        p.getId(), p.getTitle(), p.getSubject(), p.getDescription(), p.getInviteCode(),
                        memberService.getAcceptedMembers(p.getId())
                ))
                .collect(Collectors.toList());
    }

    public List<ProjectResponseDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(p -> new ProjectResponseDto(
                        p.getId(), p.getTitle(), p.getSubject(), p.getDescription(), p.getInviteCode(),
                        memberService.getAcceptedMembers(p.getId())
                ))
                .collect(Collectors.toList());
    }

    public ProjectResponseDto getProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));
        return new ProjectResponseDto(
                project.getId(), project.getTitle(), project.getSubject(), project.getDescription(), project.getInviteCode(),
                memberService.getAcceptedMembers(project.getId())
        );
    }
}
