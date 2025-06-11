package com.scheck.scheck.dto.studyGroup;

import com.scheck.scheck.dto.user.UserResponseDto;
import com.scheck.scheck.entity.studyGroup.StudyGroup;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class StudyGroupResponseDto {
    private Long groupId;
    private String groupName;
    private String description;
    private UserResponseDto leader;
    private LocalTime attendanceTime;
    private String attendanceDays;
    private Boolean isAutoApprove;
    private LocalDateTime createdAt;
    private Integer memberCount;

    public static StudyGroupResponseDto from(StudyGroup studyGroup) {
        return StudyGroupResponseDto.builder()
                .groupId(studyGroup.getGroupId())
                .groupName(studyGroup.getGroupName())
                .description(studyGroup.getDescription())
                .leader(UserResponseDto.from(studyGroup.getLeader()))
                .attendanceTime(studyGroup.getAttendanceTime())
                .attendanceDays(studyGroup.getAttendanceDays())
                .isAutoApprove(studyGroup.getIsAutoApprove())
                .createdAt(studyGroup.getCreatedAt())
                .build();
    }

    public static StudyGroupResponseDto fromWithMemberCount(StudyGroup studyGroup, Integer memberCount) {
        return StudyGroupResponseDto.builder()
                .groupId(studyGroup.getGroupId())
                .groupName(studyGroup.getGroupName())
                .description(studyGroup.getDescription())
                .leader(UserResponseDto.from(studyGroup.getLeader()))
                .attendanceTime(studyGroup.getAttendanceTime())
                .attendanceDays(studyGroup.getAttendanceDays())
                .isAutoApprove(studyGroup.getIsAutoApprove())
                .createdAt(studyGroup.getCreatedAt())
                .memberCount(memberCount)
                .build();
    }


}
