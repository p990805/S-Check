package com.scheck.scheck.controller.controller;

import com.scheck.scheck.dto.attendance.*;
import com.scheck.scheck.service.attendance.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // 출석 체크
    @PostMapping("/groups/{groupId}/check")
    public ResponseEntity<AttendanceResponseDto> checkAttendance(
            @PathVariable Long groupId,
            @RequestHeader("User-Id") Long userId,
            @RequestBody AttendanceCheckRequestDto requestDto) {
        AttendanceResponseDto response = attendanceService.checkAttendance(groupId, userId, requestDto);
        return ResponseEntity.ok(response);
    }

    // 특정 그룹의 특정 날짜 출석 현황 조회
    @GetMapping("/groups/{groupId}/status")
    public ResponseEntity<GroupAttendanceStatusDto> getGroupAttendanceStatus(
            @PathVariable Long groupId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestHeader("User-Id") Long userId) {
        GroupAttendanceStatusDto response = attendanceService.getGroupAttendanceStatus(groupId, date, userId);
        return ResponseEntity.ok(response);
    }

    // 특정 그룹의 오늘 출석 현황 조회
    @GetMapping("/groups/{groupId}/today")
    public ResponseEntity<GroupAttendanceStatusDto> getGroupTodayAttendance(
            @PathVariable Long groupId,
            @RequestHeader("User-Id") Long userId) {
        LocalDate today = LocalDate.now();
        GroupAttendanceStatusDto response = attendanceService.getGroupAttendanceStatus(groupId, today, userId);
        return ResponseEntity.ok(response);
    }

    // 오늘 내 출석 현황 조회
    @GetMapping("/my/today")
    public ResponseEntity<MyAttendanceStatusDto> getMyTodayAttendance(
            @RequestHeader("User-Id") Long userId) {
        MyAttendanceStatusDto response = attendanceService.getMyTodayAttendance(userId);
        return ResponseEntity.ok(response);
    }

    // 특정 사용자의 특정 그룹 출석 통계 조회
    @GetMapping("/groups/{groupId}/users/{targetUserId}/stats")
    public ResponseEntity<AttendanceStatsDto> getUserAttendanceStats(
            @PathVariable Long groupId,
            @PathVariable Long targetUserId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestHeader("User-Id") Long userId) {

        // 기본값 설정 (최근 3개월)
        if (endDate == null) endDate = LocalDate.now();
        if (startDate == null) startDate = endDate.minusMonths(3);

        AttendanceStatsDto response = attendanceService.getUserAttendanceStats(groupId, targetUserId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    // 내 출석 통계 조회
    @GetMapping("/groups/{groupId}/my/stats")
    public ResponseEntity<AttendanceStatsDto> getMyAttendanceStats(
            @PathVariable Long groupId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestHeader("User-Id") Long userId) {

        // 기본값 설정 (최근 3개월)
        if (endDate == null) endDate = LocalDate.now();
        if (startDate == null) startDate = endDate.minusMonths(3);

        AttendanceStatsDto response = attendanceService.getUserAttendanceStats(groupId, userId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    // 특정 그룹의 모든 멤버 출석 통계 조회 (리더용)
    @GetMapping("/groups/{groupId}/stats")
    public ResponseEntity<List<AttendanceStatsDto>> getGroupAttendanceStats(
            @PathVariable Long groupId,
            @RequestHeader("User-Id") Long userId) {
        List<AttendanceStatsDto> response = attendanceService.getGroupAttendanceStats(groupId, userId);
        return ResponseEntity.ok(response);
    }

    // 출석 기록 삭제 (리더용)
    @DeleteMapping("/{attendanceId}")
    public ResponseEntity<Void> deleteAttendance(
            @PathVariable Long attendanceId,
            @RequestHeader("User-Id") Long userId) {
        attendanceService.deleteAttendance(attendanceId, userId);
        return ResponseEntity.ok().build();
    }

    // 수동으로 출석 기록 생성 (리더용 - 특정 날짜)
    @PostMapping("/groups/{groupId}/create")
    public ResponseEntity<Void> createAttendanceForDate(
            @PathVariable Long groupId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestHeader("User-Id") Long userId) {
        attendanceService.createDailyAttendance(date);
        return ResponseEntity.ok().build();
    }
}