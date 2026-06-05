package com.example.demo.members.service;

import com.example.demo.members.domain.Member;
import com.example.demo.members.domain.User;
import com.example.demo.members.dto.MemberRequestDto;
import com.example.demo.members.dto.MemberResponseDto;
import com.example.demo.members.repository.MemberRepository;
import com.example.demo.members.repository.UserRepository;
import com.example.demo.projects.domain.Project;
import com.example.demo.projects.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public MemberService(MemberRepository memberRepository, UserRepository userRepository, ProjectRepository projectRepository) {
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    public MemberResponseDto inviteUser(Long projectId, MemberRequestDto requestDto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        if (!project.getInviteCode().equals(requestDto.getInviteCode())) {
            throw new IllegalArgumentException("초대 코드가 일치하지 않습니다.");
        }

        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Optional<Member> existingMember = memberRepository.findByProjectIdAndUserId(projectId, user.getId());
        if (existingMember.isPresent()) {
            throw new IllegalArgumentException("이미 프로젝트 가입 요청이 진행 중이거나 멤버로 등록된 상태입니다.");
        }

        Member member = new Member(null, projectId, user.getId(), user.getName(), "PENDING");
        memberRepository.save(member);

        return toDto(member, user.getUsername());
    }

    public MemberResponseDto acceptInvitation(Long projectId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Member member = memberRepository.findByProjectIdAndUserId(projectId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 프로젝트의 가입요청(PENDING) 기록을 찾을 수 없습니다."));

        if ("ACCEPTED".equals(member.getStatus())) {
            throw new IllegalArgumentException("이미 가입이 완료된 상태입니다.");
        }

        member.setStatus("ACCEPTED");
        memberRepository.save(member);

        user.getProjectIds().add(projectId);
        userRepository.save(user);

        return toDto(member, user.getUsername());
    }

    public MemberResponseDto addCreatorAsAcceptedMember(Long projectId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 생성자 아이디입니다."));

        Member member = new Member(null, projectId, user.getId(), user.getName(), "ACCEPTED");
        memberRepository.save(member);

        user.getProjectIds().add(projectId);
        userRepository.save(user);

        return toDto(member, user.getUsername());
    }

    public List<MemberResponseDto> getAcceptedMembers(Long projectId) {
        return memberRepository.findByProjectId(projectId).stream()
                .filter(m -> "ACCEPTED".equals(m.getStatus()))
                .map(m -> {
                    User user = userRepository.findById(m.getUserId()).orElse(null);
                    String username = user != null ? user.getUsername() : "unknown";
                    return toDto(m, username);
                })
                .collect(Collectors.toList());
    }

    private MemberResponseDto toDto(Member member, String username) {
        return new MemberResponseDto(
                member.getId(),
                member.getProjectId(),
                member.getUserId(),
                username,
                member.getName(),
                member.getStatus()
        );
    }
}
