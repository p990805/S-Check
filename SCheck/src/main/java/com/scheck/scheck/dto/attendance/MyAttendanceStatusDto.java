package com.scheck.scheck.dto.attendance;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

// 내 출석 현황 DTO
@Getter
@Builder
public class MyAttendanceStatusDto {
    private LocalDate date;
    private List<AttendanceResponseDto> myAttendanceList; // 오늘 출석해야 할 그룹들
    private Integer totalGroups;
    private Integer checkedGroups;
    private Integer uncheckedGroups;

    public static MyAttendanceStatusDto of(LocalDate date, List<AttendanceResponseDto> attendanceList) {
        int checkedCount = (int) attendanceList.stream()
                .mapToLong(a -> !"ABSENT".equals(a.getStatus()) ? 1 : 0).sum();

        return MyAttendanceStatusDto.builder()
                .date(date)
                .myAttendanceList(attendanceList)
                .totalGroups(attendanceList.size())
                .checkedGroups(checkedCount)
                .uncheckedGroups(attendanceList.size() - checkedCount)
                .build();
    }
}