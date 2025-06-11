package com.scheck.scheck.controller.studyGroup;

import com.scheck.scheck.dto.studyGroup.StudyGroupCreateRequestDto;
import com.scheck.scheck.dto.studyGroup.StudyGroupResponseDto;
import com.scheck.scheck.dto.studyGroup.StudyGroupSearchResponseDto;
import com.scheck.scheck.dto.studyGroup.StudyGroupUpdateRequestDto;
import com.scheck.scheck.service.studyGroup.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;

@RestController
@RequestMapping("/api/study-groups")
@RequiredArgsConstructor
public class StudyGroupController {
    private final StudyGroupService studyGroupService;

    // 스터디 그룹 생성
    @PostMapping
    public ResponseEntity<StudyGroupResponseDto> createStudyGroup(
            @RequestHeader("User-Id") Long userId, // 임시로 헤더로 사용자 ID 받음 (나중에 JWT로 변경)
            @RequestBody StudyGroupCreateRequestDto requestDto) {
        StudyGroupResponseDto response = studyGroupService.createStudyGroup(userId, requestDto);
        return ResponseEntity.ok(response);
    }

    // 스터디 그룹 상세 조회
    @GetMapping("/{groupId}")
    public ResponseEntity<StudyGroupResponseDto> getStudyGroup(@PathVariable Long groupId) {
        StudyGroupResponseDto response = studyGroupService.getStudyGroup(groupId);
        return ResponseEntity.ok(response);
    }

    // 스터디 그룹 수정
    @PutMapping("/{groupId}")
    public ResponseEntity<StudyGroupResponseDto> updateStudyGroup(
            @PathVariable Long groupId,
            @RequestHeader("User-Id") Long userId,
            @RequestBody StudyGroupUpdateRequestDto requestDto) {
        StudyGroupResponseDto response = studyGroupService.updateStudyGroup(groupId, userId, requestDto);
        return ResponseEntity.ok(response);
    }

    // 스터디 그룹 삭제
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteStudyGroup(
            @PathVariable Long groupId,
            @RequestHeader("User-Id") Long userId) {
        studyGroupService.deleteStudyGroup(groupId, userId);
        return ResponseEntity.ok().build();
    }

    // 내가 리더인 스터디 그룹 목록
    @GetMapping("/my/leading")
    public ResponseEntity<List<StudyGroupResponseDto>> getMyLeadGroups(
            @RequestHeader("User-Id") Long userId) {
        List<StudyGroupResponseDto> response = studyGroupService.getMyLeadGroups(userId);
        return ResponseEntity.ok(response);
    }

    // 내가 속한 스터디 그룹 목록
    @GetMapping("/my/joined")
    public ResponseEntity<List<StudyGroupResponseDto>> getMyJoinedGroups(
            @RequestHeader("User-Id") Long userId) {
        List<StudyGroupResponseDto> response = studyGroupService.getMyJoinedGroups(userId);
        return ResponseEntity.ok(response);
    }

    // 스터디 그룹 검색
    @GetMapping("/search")
    public ResponseEntity<Page<StudyGroupSearchResponseDto>> searchStudyGroups(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<StudyGroupSearchResponseDto> response = studyGroupService.searchStudyGroups(keyword, pageable);
        return ResponseEntity.ok(response);
    }

    // 전체 스터디 그룹 목록
    @GetMapping
    public ResponseEntity<Page<StudyGroupSearchResponseDto>> getAllStudyGroups(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<StudyGroupSearchResponseDto> response = studyGroupService.getAllStudyGroups(pageable);
        return ResponseEntity.ok(response);
    }




}
