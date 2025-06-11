package com.scheck.scheck.dto.attendance;

import lombok.Builder;
import lombok.Getter;

// 출석 통계 DTO
@Getter
@Builder
public class AttendanceStatsDto {
    private Long userId;
    private String nickname;
    private Long totalDays;       // 총 출석해야 할 날
    private Long presentDays;     // 출석 일수
    private Long lateDays;        // 지각 일수
    private Long absentDays;      // 결석 일수
    private Long submittedDays;   // 제출 일수
    private Double attendanceRate; // 출석률 (출석+지각+제출)/총일수

    public static AttendanceStatsDto of(Long userId, String nickname,
                                        Long totalDays, Long presentDays, Long lateDays, Long submittedDays) {
        Long absentDays = totalDays - presentDays - lateDays - submittedDays;
        Double attendanceRate = totalDays > 0 ?
                (double)(presentDays + lateDays + submittedDays) / totalDays * 100 : 0.0;

        return AttendanceStatsDto.builder()
                .userId(userId)
                .nickname(nickname)
                .totalDays(totalDays)
                .presentDays(presentDays)
                .lateDays(lateDays)
                .absentDays(absentDays)
                .submittedDays(submittedDays)
                .attendanceRate(Math.round(attendanceRate * 100.0) / 100.0) // 소수점 2자리
                .build();
    }
}
