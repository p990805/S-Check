package com.scheck.scheck.controller;

import com.scheck.scheck.dto.user.SignUpRequestDto;
import com.scheck.scheck.dto.user.SignUpResponseDto;
import com.scheck.scheck.exception.DuplicateEmailException;
import com.scheck.scheck.response.ApiResponse;
import com.scheck.scheck.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponseDto>> signUp(
            @Valid @RequestBody SignUpRequestDto signUpRequest) {

        try {
            SignUpResponseDto response = userService.signUp(signUpRequest);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (DuplicateEmailException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("DUPLICATE_EMAIL", e.getMessage()));
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailExists(
            @RequestParam String email) {

        boolean exists = userService.isEmailExists(email);
        return ResponseEntity.ok(ApiResponse.success(!exists)); // 사용 가능하면 true
    }
}