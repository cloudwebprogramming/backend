package com.example.demo.members.controller;

import com.example.demo.members.dto.MemberRequestDto;
import com.example.demo.members.dto.MemberResponseDto;
import com.example.demo.members.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/members")
@CrossOrigin("*")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<?> inviteUser(@PathVariable Long projectId, @RequestBody MemberRequestDto requestDto) {
        try {
            MemberResponseDto response = memberService.inviteUser(projectId, requestDto);
            return ResponseEntity.status(201).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{username}/accept")
    public ResponseEntity<?> acceptInvitation(@PathVariable Long projectId, @PathVariable String username) {
        try {
            MemberResponseDto response = memberService.acceptInvitation(projectId, username);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<String>> getProjectMemberNames(@PathVariable Long projectId) {
        List<String> names = memberService.getAcceptedMembers(projectId).stream()
                .map(MemberResponseDto::getName)
                .collect(Collectors.toList());
        return ResponseEntity.ok(names);
    }

    @GetMapping("/details")
    public ResponseEntity<List<MemberResponseDto>> getProjectMemberDetails(@PathVariable Long projectId) {
        return ResponseEntity.ok(memberService.getAcceptedMembers(projectId));
    }
}
