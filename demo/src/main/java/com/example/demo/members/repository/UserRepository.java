package com.example.demo.members.repository;

import com.example.demo.members.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByStudentId(String studentId);
    List<User> findAll();
    void clear();
}
