package com.scheck.scheck.controller.user;

import com.scheck.scheck.dto.user.KakaoLoginRequestDto;
import com.scheck.scheck.dto.user.LoginResponseDto;
import com.scheck.scheck.dto.user.UserResponseDto;
import com.scheck.scheck.dto.user.UserUpdateRequestDto;
import com.scheck.scheck.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login/kakao")
    public ResponseEntity<LoginResponseDto> kakaoLogin(@RequestBody KakaoLoginRequestDto requestDto){
        LoginResponseDto loginResponse = userService.loginOrRegister(requestDto);
        return ResponseEntity.ok(loginResponse);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequestDto requestDto){
        UserResponseDto userResponse = userService.updateUser(userId, requestDto);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/exists/kakao/{kakaoId}")
    public ResponseEntity<Boolean> existsByKakaoId(@PathVariable String kakaoId) {
        boolean exists = userService.existsByKakaoId(kakaoId);
        return ResponseEntity.ok(exists);
    }

    // 토큰 갱신 엔드포인트
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        // Bearer 접두사 제거
        String token = refreshToken.replace("Bearer ", "");
        LoginResponseDto loginResponse = userService.refreshToken(token);
        return ResponseEntity.ok(loginResponse);
    }
}