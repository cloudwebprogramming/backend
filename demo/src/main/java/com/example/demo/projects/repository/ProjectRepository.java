package com.example.demo.projects.repository;

import com.example.demo.projects.domain.Project;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    Project save(Project project);
    Optional<Project> findById(Long id);
    Optional<Project> findByInviteCode(String inviteCode);
    List<Project> findAll();
    void clear();
}
