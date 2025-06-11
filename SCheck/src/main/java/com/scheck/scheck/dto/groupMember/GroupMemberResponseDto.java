package com.scheck.scheck.dto.groupMember;

import com.scheck.scheck.dto.user.UserResponseDto;
import com.scheck.scheck.entity.groupMember.GroupMember;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GroupMemberResponseDto {
    private Long memberId;
    private UserResponseDto user;
    private String status;
    private LocalDateTime appliedAt;
    private LocalDateTime approvedAt;
    private UserResponseDto approvedBy;
    private boolean isLeader;

    public static GroupMemberResponseDto from(GroupMember groupMember) {
        return GroupMemberResponseDto.builder()
                .memberId(groupMember.getMemberId())
                .user(UserResponseDto.from(groupMember.getUser()))
                .status(groupMember.getStatus().name())
                .appliedAt(groupMember.getAppliedAt())
                .approvedAt(groupMember.getApprovedAt())
                .approvedBy(groupMember.getApprovedBy() != null ?
                        UserResponseDto.from(groupMember.getApprovedBy()) : null)
                .isLeader(groupMember.isLeader())
                .build();
    }
}