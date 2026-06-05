package com.example.demo.members.domain;

import java.util.ArrayList;
import java.util.List;

public class User {
    private Long id;
    private String username;
    private String name;
    private String email;
    private List<Long> projectIds = new ArrayList<>();

    public User() {}

    public User(Long id, String username, String name, String email) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Long> getProjectIds() { return projectIds; }
    public void setProjectIds(List<Long> projectIds) { this.projectIds = projectIds; }
}
