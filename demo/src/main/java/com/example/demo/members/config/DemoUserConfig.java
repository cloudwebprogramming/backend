package com.example.demo.members.config;

import com.example.demo.deployment.task.domain.Task;
import com.example.demo.deployment.task.repository.TaskRepository;
import com.example.demo.members.domain.Member;
import com.example.demo.members.domain.User;
import com.example.demo.members.repository.MemberRepository;
import com.example.demo.members.repository.UserRepository;
import com.example.demo.members.service.PasswordEncoder;
import com.example.demo.projects.domain.Project;
import com.example.demo.projects.repository.ProjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DemoUserConfig {

    @Bean
    public CommandLineRunner demoUserInitializer(UserRepository userRepository,
                                                 MemberRepository memberRepository,
                                                 ProjectRepository projectRepository,
                                                 TaskRepository taskRepository,
                                                 PasswordEncoder passwordEncoder) {
        return args -> {
            User user = userRepository.findByEmail("cloudweb@kangnam.ac.kr")
                    .orElseGet(User::new);

            if (user.getId() == null) {
                user.setUsername("cloudweb");
                user.setEmail("cloudweb@kangnam.ac.kr");
                user.setPassword(passwordEncoder.encode("1234"));
                user.setSchoolId("cloudweb");
                user.setStudentId("202204000");
                user.setCreatedAt(LocalDateTime.now());
            }

            user.setName("홍길동");
            user.setEmailVerified(true);
            user.setUpdatedAt(LocalDateTime.now());

            User savedUser = userRepository.save(user);

            if (!projectRepository.findAll().isEmpty()) {
                return;
            }

            Project project = projectRepository.save(new Project(
                    null,
                    "팀프로젝트",
                    "클라우드 기반 웹개발",
                    "팀프로젝트 웹 개발",
                    6,
                    LocalDate.of(2026, 6, 16),
                    "DEMO-CLOUD"
            ));

            memberRepository.save(new Member(null, project.getId(), savedUser.getId(), savedUser.getName(), "ACCEPTED"));
            savedUser.getProjectIds().add(project.getId());
            userRepository.save(savedUser);

            Task planningTask = new Task(
                    null,
                    project.getId(),
                    "요구사항 정리",
                    "팀프로젝트 웹 개발 범위와 화면 흐름을 정리합니다.",
                    "홍길동",
                    "기획",
                    "높음",
                    LocalDate.now().plusDays(3)
            );
            planningTask.setDetailNotes("메인 화면, 프로젝트 상세, 할 일 내부 페이지의 필수 기능을 정리합니다.");
            planningTask.setChecklist(List.of("[x] 핵심 기능 목록 작성", "[ ] 화면 흐름 정리", "[ ] 발표용 요구사항 요약"));
            planningTask.setStatus("완료");
            taskRepository.save(planningTask);

            Task frontendTask = new Task(
                    null,
                    project.getId(),
                    "프론트엔드 UI 구현",
                    "프로젝트 목록, 일정표, 할 일 페이지 UI를 구현합니다.",
                    "홍길동",
                    "프론트엔드",
                    "높음",
                    LocalDate.now().plusDays(7)
            );
            frontendTask.setDetailNotes("Notion 페이지처럼 프로젝트 상세와 할 일 내부 페이지를 분리합니다.");
            frontendTask.setChecklist(List.of("[x] 프로젝트 목록 카드 정리", "[ ] 일정표 탭 구현 확인", "[ ] 모바일 레이아웃 점검"));
            frontendTask.setStatus("진행");
            taskRepository.save(frontendTask);

            Task backendTask = new Task(
                    null,
                    project.getId(),
                    "백엔드 API 연동",
                    "프로젝트와 할 일 데이터를 API로 조회하고 저장합니다.",
                    "전체",
                    "백엔드",
                    "보통",
                    LocalDate.now().plusDays(10)
            );
            backendTask.setDetailNotes("메모리 저장소 기반으로 데모 데이터를 제공하고 프론트와 연동합니다.");
            backendTask.setChecklist(List.of("[x] 프로젝트 생성 API 확인", "[x] 할 일 생성 API 확인", "[ ] 상세 저장 API 시연"));
            backendTask.setStatus("진행");
            taskRepository.save(backendTask);

            Task presentationTask = new Task(
                    null,
                    project.getId(),
                    "발표자료 제작",
                    "팀프로젝트 관리 웹의 주요 기능을 발표자료로 정리합니다.",
                    "",
                    "발표자료",
                    "보통",
                    LocalDate.now().plusDays(14)
            );
            presentationTask.setDetailNotes("시연 순서는 로그인, 프로젝트 진입, 일정표 확인, 할 일 내부 페이지 작성 순서로 구성합니다.");
            presentationTask.setChecklist(List.of("[ ] 기능 소개 슬라이드", "[ ] 시연 순서 작성", "[ ] 역할 분담 정리"));
            presentationTask.setStatus("예정");
            taskRepository.save(presentationTask);
        };
    }
}
