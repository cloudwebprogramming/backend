package com.example.demo.projects;

import com.example.demo.members.domain.User;
import com.example.demo.members.repository.MemoryMemberRepository;
import com.example.demo.members.repository.MemoryUserRepository;
import com.example.demo.members.service.MemberService;
import com.example.demo.projects.dto.ProjectRequestDto;
import com.example.demo.projects.dto.ProjectResponseDto;
import com.example.demo.projects.repository.MemoryProjectRepository;
import com.example.demo.projects.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectServiceTest {

    private ProjectService projectService;
    private MemoryProjectRepository projectRepository;
    private MemoryMemberRepository memberRepository;
    private MemoryUserRepository userRepository;

    @BeforeEach
    void setUp() {
        projectRepository = new MemoryProjectRepository();
        memberRepository = new MemoryMemberRepository();
        userRepository = new MemoryUserRepository();
        projectRepository.clear();
        memberRepository.clear();
        userRepository.clear();

        MemberService memberService = new MemberService(memberRepository, userRepository, projectRepository);
        projectService = new ProjectService(projectRepository, memberService);

        // 참여 테스트용 사용자 등록
        userRepository.save(new User(null, "dongwoo", "서동우", "dongwoo@example.com"));
    }

    @Test
    void testCreateProject_GeneratesInviteCode() {
        ProjectRequestDto request = new ProjectRequestDto("클라우드 팀플", "클라우드웹개발", "설명", null, null);

        ProjectResponseDto response = projectService.createProject(request);

        assertNotNull(response.getId());
        assertEquals("클라우드 팀플", response.getTitle());
        assertNotNull(response.getInviteCode());
        assertTrue(response.getInviteCode().startsWith("INV-"));
    }

    @Test
    void testJoinByInviteCode_Success() {
        ProjectResponseDto created = projectService.createProject(
                new ProjectRequestDto("참여 대상 프로젝트", "DB", "설명", null, null));
        String inviteCode = created.getInviteCode();

        ProjectResponseDto joined = projectService.joinByInviteCode(inviteCode, "dongwoo");

        assertEquals(created.getId(), joined.getId());
        assertTrue(joined.getMembers().stream()
                .anyMatch(m -> "dongwoo".equals(m.getUsername()) && "ACCEPTED".equals(m.getStatus())));
    }

    @Test
    void testJoinByInviteCode_InvalidCode() {
        assertThrows(IllegalArgumentException.class, () ->
                projectService.joinByInviteCode("INV-NOPE12", "dongwoo"));
    }

    @Test
    void testJoinByInviteCode_DuplicateJoin() {
        ProjectResponseDto created = projectService.createProject(
                new ProjectRequestDto("중복 참여 프로젝트", "DB", "설명", null, null));
        String inviteCode = created.getInviteCode();

        projectService.joinByInviteCode(inviteCode, "dongwoo");

        assertThrows(IllegalArgumentException.class, () ->
                projectService.joinByInviteCode(inviteCode, "dongwoo"));
    }

    @Test
    void testJoinByInviteCode_UnknownUser() {
        ProjectResponseDto created = projectService.createProject(
                new ProjectRequestDto("프로젝트", "과목", "설명", null, null));

        assertThrows(IllegalArgumentException.class, () ->
                projectService.joinByInviteCode(created.getInviteCode(), "ghost"));
    }
}
