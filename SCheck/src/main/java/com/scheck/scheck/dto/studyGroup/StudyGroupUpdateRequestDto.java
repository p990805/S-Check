package com.scheck.scheck.dto.studyGroup;

import lombok.Getter;

import java.time.LocalTime;

//스터디 그룹 수정 요청 DTO
@Getter
public class StudyGroupUpdateRequestDto {
    private String groupName;
    private String description;
    private LocalTime attendanceTime;
    private String attendanceDays;
    private Boolean isAutoApprove;
}
