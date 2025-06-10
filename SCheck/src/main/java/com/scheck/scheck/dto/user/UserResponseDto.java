package com.scheck.scheck.dto.user;

import com.scheck.scheck.entity.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponseDto {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private LocalDateTime createdAt;

    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
