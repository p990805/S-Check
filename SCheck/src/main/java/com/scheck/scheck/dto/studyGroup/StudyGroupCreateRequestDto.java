package com.scheck.scheck.dto.studyGroup;

import lombok.Getter;

import java.time.LocalTime;

// 스터디 그룹 생성 요청 DTO
@Getter
public class StudyGroupCreateRequestDto {
    private String groupName;
    private String description;
    private LocalTime attendanceTime;
    private String attendanceDays;
    private Boolean isAutoApprove;
}
