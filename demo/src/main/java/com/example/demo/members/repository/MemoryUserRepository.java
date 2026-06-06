package com.example.demo.members.repository;

import com.example.demo.members.domain.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryUserRepository implements UserRepository {
    private final List<User> database = new CopyOnWriteArrayList<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idSequence.getAndIncrement());
        } else {
            findById(user.getId()).ifPresent(database::remove);
        }
        database.add(user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return database.stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null) return Optional.empty();
        return database.stream()
                .filter(u -> username.equalsIgnoreCase(u.getUsername()))
                .findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return database.stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public Optional<User> findByStudentId(String studentId) {
        if (studentId == null) return Optional.empty();
        return database.stream()
                .filter(u -> u.getStudentId() != null && u.getStudentId().equals(studentId))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(database);
    }

    @Override
    public void clear() {
        database.clear();
        idSequence.set(1);
    }
}
