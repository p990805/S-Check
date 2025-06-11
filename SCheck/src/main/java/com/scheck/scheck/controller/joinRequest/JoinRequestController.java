package com.scheck.scheck.controller.joinRequest;

import com.scheck.scheck.dto.joinRequest.JoinRequestCreateDto;
import com.scheck.scheck.dto.joinRequest.JoinRequestProcessDto;
import com.scheck.scheck.dto.joinRequest.JoinRequestResponseDto;
import com.scheck.scheck.dto.joinRequest.JoinRequestSummaryDto;
import com.scheck.scheck.service.joinRequest.JoinRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/join-requests")
@RequiredArgsConstructor
public class JoinRequestController {

    private final JoinRequestService joinRequestService;

    // 가입 신청
    @PostMapping
    public ResponseEntity<JoinRequestResponseDto> createJoinRequest(
            @RequestHeader("User-Id") Long userId,
            @RequestBody JoinRequestCreateDto requestDto) {
        JoinRequestResponseDto response = joinRequestService.createJoinRequest(userId, requestDto);
        return ResponseEntity.ok(response);
    }

    // 가입 신청 처리 (승인/거절)
    @PutMapping("/{requestId}/process")
    public ResponseEntity<JoinRequestResponseDto> processJoinRequest(
            @PathVariable Long requestId,
            @RequestHeader("User-Id") Long userId,
            @RequestBody JoinRequestProcessDto processDto) {
        JoinRequestResponseDto response = joinRequestService.processJoinRequest(requestId, userId, processDto);
        return ResponseEntity.ok(response);
    }

    // 특정 그룹의 대기 중인 가입 신청 목록 (리더용)
    @GetMapping("/group/{groupId}/pending")
    public ResponseEntity<List<JoinRequestSummaryDto>> getPendingRequestsByGroup(
            @PathVariable Long groupId,
            @RequestHeader("User-Id") Long userId) {
        List<JoinRequestSummaryDto> response = joinRequestService.getPendingRequestsByGroup(groupId, userId);
        return ResponseEntity.ok(response);
    }

    // 내가 신청한 가입 요청 목록
    @GetMapping("/my")
    public ResponseEntity<List<JoinRequestSummaryDto>> getMyJoinRequests(
            @RequestHeader("User-Id") Long userId) {
        List<JoinRequestSummaryDto> response = joinRequestService.getMyJoinRequests(userId);
        return ResponseEntity.ok(response);
    }

    // 가입 신청 취소
    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> cancelJoinRequest(
            @PathVariable Long requestId,
            @RequestHeader("User-Id") Long userId) {
        joinRequestService.cancelJoinRequest(requestId, userId);
        return ResponseEntity.ok().build();
    }
}