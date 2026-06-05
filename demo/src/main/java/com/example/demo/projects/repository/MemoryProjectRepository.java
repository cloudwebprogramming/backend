package com.example.demo.projects.repository;

import com.example.demo.projects.domain.Project;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryProjectRepository implements ProjectRepository {
    private final List<Project> database = new CopyOnWriteArrayList<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    @Override
    public Project save(Project project) {
        if (project.getId() == null) {
            project.setId(idSequence.getAndIncrement());
        } else {
            database.removeIf(p -> p.getId().equals(project.getId()));
        }
        database.add(project);
        return project;
    }

    @Override
    public Optional<Project> findById(Long id) {
        return database.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<Project> findByInviteCode(String inviteCode) {
        if (inviteCode == null) return Optional.empty();
        return database.stream()
                .filter(p -> inviteCode.equals(p.getInviteCode()))
                .findFirst();
    }

    @Override
    public List<Project> findAll() {
        return new ArrayList<>(database);
    }

    @Override
    public void clear() {
        database.clear();
        idSequence.set(1);
    }
}
