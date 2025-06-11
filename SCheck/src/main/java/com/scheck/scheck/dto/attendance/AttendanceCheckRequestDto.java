package com.scheck.scheck.dto.attendance;

import lombok.Getter;

// 출석 체크 요청 DTO
@Getter
public class AttendanceCheckRequestDto {
    private String type; // "PRESENT", "LATE", "SUBMITTED"
}
