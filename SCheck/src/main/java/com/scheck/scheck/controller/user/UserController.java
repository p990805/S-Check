package com.scheck.scheck.controller.user;

import com.scheck.scheck.dto.user.KakaoLoginRequestDto;
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
    public ResponseEntity<UserResponseDto> kakaoLogin(@RequestBody KakaoLoginRequestDto requestDto){
        UserResponseDto userResponse = userService.loginOrRegister(requestDto);
        return ResponseEntity.ok(userResponse);
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
}
