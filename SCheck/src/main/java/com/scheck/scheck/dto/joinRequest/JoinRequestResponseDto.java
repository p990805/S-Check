package com.scheck.scheck.dto.joinRequest;

import com.scheck.scheck.dto.studyGroup.StudyGroupResponseDto;
import com.scheck.scheck.dto.user.UserResponseDto;
import com.scheck.scheck.entity.joinRequest.JoinRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class JoinRequestResponseDto {
    private Long requestId;
    private StudyGroupResponseDto studyGroup;
    private UserResponseDto user;
    private String status;
    private String message;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private UserResponseDto processedBy;
    private String rejectReason;

    public static JoinRequestResponseDto from(JoinRequest joinRequest) {
        return JoinRequestResponseDto.builder()
                .requestId(joinRequest.getRequestId())
                .studyGroup(StudyGroupResponseDto.from(joinRequest.getStudyGroup()))
                .user(UserResponseDto.from(joinRequest.getUser()))
                .status(joinRequest.getStatus().name())
                .message(joinRequest.getMessage())
                .requestedAt(joinRequest.getRequestedAt())
                .processedAt(joinRequest.getProcessedAt())
                .processedBy(joinRequest.getProcessedBy() != null ?
                        UserResponseDto.from(joinRequest.getProcessedBy()) : null)
                .rejectReason(joinRequest.getRejectReason())
                .build();
    }
}