package com.scheck.scheck.dto.attendance;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

// 그룹 출석 현황 DTO
@Getter
@Builder
public class GroupAttendanceStatusDto {
    private LocalDate date;
    private String groupName;
    private Long totalMembers;
    private Long presentCount;
    private Long lateCount;
    private Long absentCount;
    private Long submittedCount;
    private List<AttendanceResponseDto> attendanceList;

    public static GroupAttendanceStatusDto of(LocalDate date, String groupName,
                                              List<AttendanceResponseDto> attendanceList) {
        long presentCount = attendanceList.stream()
                .mapToLong(a -> "PRESENT".equals(a.getStatus()) ? 1 : 0).sum();
        long lateCount = attendanceList.stream()
                .mapToLong(a -> "LATE".equals(a.getStatus()) ? 1 : 0).sum();
        long absentCount = attendanceList.stream()
                .mapToLong(a -> "ABSENT".equals(a.getStatus()) ? 1 : 0).sum();
        long submittedCount = attendanceList.stream()
                .mapToLong(a -> "SUBMITTED".equals(a.getStatus()) ? 1 : 0).sum();

        return GroupAttendanceStatusDto.builder()
                .date(date)
                .groupName(groupName)
                .totalMembers((long) attendanceList.size())
                .presentCount(presentCount)
                .lateCount(lateCount)
                .absentCount(absentCount)
                .submittedCount(submittedCount)
                .attendanceList(attendanceList)
                .build();
    }
}
