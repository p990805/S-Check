package com.scheck.scheck.dto.joinRequest;

import lombok.Getter;

@Getter
public class JoinRequestProcessDto {
    private String action; // "APPROVE" or "REJECT"
    private String rejectReason; // 거절 시에만 사용
}
