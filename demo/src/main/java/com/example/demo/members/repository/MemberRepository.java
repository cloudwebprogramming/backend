package com.example.demo.members.repository;

import com.example.demo.members.domain.Member;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    List<Member> findByProjectId(Long projectId);
    List<Member> findByUserId(Long userId);
    Optional<Member> findByProjectIdAndUserId(Long projectId, Long userId);
    List<Member> findAll();
    void clear();
}
