package com.scheck.scheck.controller.groupMember;

import com.scheck.scheck.dto.groupMember.GroupMemberResponseDto;
import com.scheck.scheck.dto.groupMember.KickMemberRequestDto;
import com.scheck.scheck.service.joinRequest.GroupMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupMemberController {

    private final GroupMemberService groupMemberService;

    // 그룹 멤버 목록 조회
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberResponseDto>> getGroupMembers(@PathVariable Long groupId) {
        List<GroupMemberResponseDto> response = groupMemberService.getGroupMembers(groupId);
        return ResponseEntity.ok(response);
    }

    // 멤버 강퇴 (리더만 가능)
    @DeleteMapping("/{groupId}/members/{targetUserId}")
    public ResponseEntity<Void> kickMember(
            @PathVariable Long groupId,
            @PathVariable Long targetUserId,
            @RequestHeader("User-Id") Long userId,
            @RequestBody KickMemberRequestDto requestDto) {
        groupMemberService.kickMember(groupId, targetUserId, userId, requestDto);
        return ResponseEntity.ok().build();
    }

    // 그룹 탈퇴
    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<Void> leaveGroup(
            @PathVariable Long groupId,
            @RequestHeader("User-Id") Long userId) {
        groupMemberService.leaveGroup(groupId, userId);
        return ResponseEntity.ok().build();
    }

    // 그룹 멤버 수 조회
    @GetMapping("/{groupId}/members/count")
    public ResponseEntity<Integer> getGroupMemberCount(@PathVariable Long groupId) {
        Integer memberCount = groupMemberService.getGroupMemberCount(groupId);
        return ResponseEntity.ok(memberCount);
    }
}
