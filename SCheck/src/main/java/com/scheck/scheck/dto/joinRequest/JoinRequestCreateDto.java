package com.scheck.scheck.dto.joinRequest;

import lombok.Getter;

// 가입 신청 생성 요청 DTO
@Getter
public class JoinRequestCreateDto {
    private Long groupId;
    private String message;
}
