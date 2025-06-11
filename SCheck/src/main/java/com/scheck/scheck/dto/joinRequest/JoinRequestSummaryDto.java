package com.scheck.scheck.dto.joinRequest;

import com.scheck.scheck.entity.joinRequest.JoinRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// 간단한 가입 신청 정보 DTO (목록용)
@Getter
@Builder
public class JoinRequestSummaryDto {
    private Long requestId;
    private String groupName;
    private String userNickname;
    private String status;
    private String message;
    private LocalDateTime requestedAt;

    public static JoinRequestSummaryDto from(JoinRequest joinRequest) {
        return JoinRequestSummaryDto.builder()
                .requestId(joinRequest.getRequestId())
                .groupName(joinRequest.getStudyGroup().getGroupName())
                .userNickname(joinRequest.getUser().getNickname())
                .status(joinRequest.getStatus().name())
                .message(joinRequest.getMessage())
                .requestedAt(joinRequest.getRequestedAt())
                .build();
    }
}
