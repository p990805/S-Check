package com.scheck.scheck.dto.user;

import com.scheck.scheck.entity.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LoginResponseDto {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;

    public static LoginResponseDto of(User user, String accessToken, String refreshToken, long expiresIn) {
        return LoginResponseDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .createdAt(user.getCreatedAt())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .build();
    }
}