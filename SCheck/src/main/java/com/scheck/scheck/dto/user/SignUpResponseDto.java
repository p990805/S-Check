package com.scheck.scheck.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SignUpResponseDto {
    private Long userId;
    private String email;
    private String name;
    private String message;
}
