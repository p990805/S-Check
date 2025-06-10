package com.scheck.scheck.service.user;

import com.scheck.scheck.dto.user.KakaoLoginRequestDto;
import com.scheck.scheck.dto.user.UserResponseDto;
import com.scheck.scheck.dto.user.UserUpdateRequestDto;
import com.scheck.scheck.entity.user.User;
import com.scheck.scheck.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponseDto loginOrRegister(KakaoLoginRequestDto requestDto){
        return userRepository.findByKakaoId((requestDto.getKakaoId()))
                .map(UserResponseDto::from)

                .orElseGet(() -> {
                    User newUser = User.builder()
                            .kakaoId(requestDto.getKakaoId())
                            .nickname(requestDto.getNickname())
                            .profileImageUrl(requestDto.getProfileImageUrl())
                            .build();
                    User savedUser = userRepository.save(newUser);
                    return UserResponseDto.from(savedUser);
                });
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
}
