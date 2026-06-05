package com.example.demo.projects.domain;

import com.example.demo.members.domain.Member;
import java.util.ArrayList;
import java.util.List;

public class Project {
    private Long id;
    private String title;
    private String subject;
    private String description;
    private String inviteCode;
    private List<Member> members = new ArrayList<>();

    public Project() {}

    public Project(Long id, String title, String subject, String description, String inviteCode) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.description = description;
        this.inviteCode = inviteCode;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
    public List<Member> getMembers() { return members; }
    public void setMembers(List<Member> members) { this.members = members; }
}
