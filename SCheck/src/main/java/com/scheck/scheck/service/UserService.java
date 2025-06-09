package com.scheck.scheck.service;

import com.scheck.scheck.dto.user.SignUpRequestDto;
import com.scheck.scheck.dto.user.SignUpResponseDto;
import com.scheck.scheck.entity.user.User;

public interface UserService {
    SignUpResponseDto signUp(SignUpRequestDto signUpRequest);
    User findByEmail(String email);
    boolean isEmailExists(String email);
}