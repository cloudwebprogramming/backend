package com.example.demo.members.repository;

import com.example.demo.members.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class MemoryMemberRepository implements MemberRepository {
    private final List<Member> database = new CopyOnWriteArrayList<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    @Override
    public Member save(Member member) {
        if (member.getId() == null) {
            member.setId(idSequence.getAndIncrement());
        } else {
            database.removeIf(m -> m.getId().equals(member.getId()));
        }
        database.add(member);
        return member;
    }

    @Override
    public List<Member> findByProjectId(Long projectId) {
        return database.stream()
                .filter(m -> m.getProjectId().equals(projectId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Member> findByUserId(Long userId) {
        return database.stream()
                .filter(m -> m.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Member> findByProjectIdAndUserId(Long projectId, Long userId) {
        return database.stream()
                .filter(m -> m.getProjectId().equals(projectId) && m.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(database);
    }

    @Override
    public void clear() {
        database.clear();
        idSequence.set(1);
    }
}
