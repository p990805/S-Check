package com.scheck.scheck.dto.user;

import lombok.Getter;

@Getter
public class KakaoLoginRequestDto {
    private String kakaoId;
    private String nickname;
    private String profileImageUrl;
}
