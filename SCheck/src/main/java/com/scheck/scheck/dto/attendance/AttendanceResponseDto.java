package com.scheck.scheck.dto.attendance;

import com.scheck.scheck.dto.user.UserResponseDto;
import com.scheck.scheck.entity.attendance.Attendance;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class AttendanceResponseDto {
    private Long attendanceId;
    private Long groupId;
    private String groupName;
    private UserResponseDto user;
    private LocalDate attendanceDate;
    private String status;
    private LocalDateTime checkedAt;
    private boolean canCheck; // 현재 출석 체크 가능 여부

    public static AttendanceResponseDto from(Attendance attendance) {
        return AttendanceResponseDto.builder()
                .attendanceId(attendance.getAttendanceId())
                .groupId(attendance.getStudyGroup().getGroupId())
                .groupName(attendance.getStudyGroup().getGroupName())
                .user(UserResponseDto.from(attendance.getUser()))
                .attendanceDate(attendance.getAttendanceDate())
                .status(attendance.getStatus().name())
                .checkedAt(attendance.getCheckedAt())
                .canCheck(attendance.canCheck(LocalDateTime.now()))
                .build();
    }
}
