package com.scheck.scheck.service;

import com.scheck.scheck.dto.user.SignUpRequestDto;
import com.scheck.scheck.dto.user.SignUpResponseDto;
import com.scheck.scheck.entity.user.AuthType;
import com.scheck.scheck.entity.user.User;
import com.scheck.scheck.exception.DuplicateEmailException;
import com.scheck.scheck.exception.UserNotFoundException;
import com.scheck.scheck.repository.user.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SignUpResponseDto signUp(SignUpRequestDto signUpRequest) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(signUpRequest.getEmail()))
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");

        // 사용자 생성
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .name(signUpRequest.getName())
                .phone(signUpRequest.getPhone())
                .authType(AuthType.EMAIL)
                .build();

        User savedUser = userRepository.save(user);

        return SignUpResponseDto.builder()
                .userId(savedUser.getUserId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .message("회원가입이 완료되었습니다.")
                .build();
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}