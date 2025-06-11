package com.scheck.scheck.dto.studyGroup;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StudyGroupSearchResponseDto {
    private Long groupId;
    private String groupName;
    private String description;
    private String leaderNickname;
    private Integer memberCount;
    private Boolean isAutoApprove;
    private LocalDateTime createdAt;
}
