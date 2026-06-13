package com.example.demo.projects.dto;

import com.example.demo.members.dto.MemberResponseDto;
import java.time.LocalDate;
import java.util.List;

public class ProjectResponseDto {
    private Long id;
    private String title;
    private String subject;
    private String description;
    private Integer memberCount;
    private LocalDate deadline;
    private String inviteCode;
    private List<MemberResponseDto> members;

    public ProjectResponseDto() {}

    public ProjectResponseDto(Long id, String title, String subject, String description, Integer memberCount, LocalDate deadline, String inviteCode, List<MemberResponseDto> members) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.description = description;
        this.memberCount = memberCount;
        this.deadline = deadline;
        this.inviteCode = inviteCode;
        this.members = members;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getMemberCount() { return memberCount; }
    public void setMemberCount(Integer memberCount) { this.memberCount = memberCount; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
    public List<MemberResponseDto> getMembers() { return members; }
    public void setMembers(List<MemberResponseDto> members) { this.members = members; }
}
