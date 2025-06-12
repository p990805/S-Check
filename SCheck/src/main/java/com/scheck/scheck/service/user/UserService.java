package com.scheck.scheck.service.user;

import com.scheck.scheck.config.JwtConfig;
import com.scheck.scheck.dto.user.KakaoLoginRequestDto;
import com.scheck.scheck.dto.user.LoginResponseDto;
import com.scheck.scheck.dto.user.UserResponseDto;
import com.scheck.scheck.dto.user.UserUpdateRequestDto;
import com.scheck.scheck.entity.user.User;
import com.scheck.scheck.repository.user.UserRepository;
import com.scheck.scheck.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;

    @Transactional
    public LoginResponseDto loginOrRegister(KakaoLoginRequestDto requestDto){
        User user = userRepository.findByKakaoId(requestDto.getKakaoId())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .kakaoId(requestDto.getKakaoId())
                            .nickname(requestDto.getNickname())
                            .profileImageUrl(requestDto.getProfileImageUrl())
                            .build();
                    return userRepository.save(newUser);
                });

        // JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(user.getUserId(), user.getKakaoId(), user.getNickname());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());

        return LoginResponseDto.of(user, accessToken, refreshToken, jwtConfig.getExpiration());
    }

    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return UserResponseDto.from(user);
    }

    @Transactional
    public UserResponseDto updateUser(Long userId, UserUpdateRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if(requestDto.getNickname() != null &&
                !requestDto.getNickname().equals(user.getNickname()) &&
                userRepository.existsByNickname(requestDto.getNickname())){
            throw new IllegalArgumentException("이미 사용 중인 닉네임 입니다.");
        }
        user.updateProfile(requestDto.getNickname(),requestDto.getProfileImageUrl());
        return UserResponseDto.from(user);
    }

    public boolean existsByKakaoId(String kakaoId) {
        return userRepository.existsByKakaoId(kakaoId);
    }

    // 토큰 갱신
    @Transactional
    public LoginResponseDto refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtUtil.generateAccessToken(user.getUserId(), user.getKakaoId(), user.getNickname());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUserId());

        return LoginResponseDto.of(user, newAccessToken, newRefreshToken, jwtConfig.getExpiration());
    }
}