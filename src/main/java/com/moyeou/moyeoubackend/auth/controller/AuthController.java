package com.moyeou.moyeoubackend.auth.controller;

import com.moyeou.moyeoubackend.auth.controller.request.LoginRequest;
import com.moyeou.moyeoubackend.auth.controller.response.LoginResponse;
import com.moyeou.moyeoubackend.auth.controller.response.RefreshResponse;
import com.moyeou.moyeoubackend.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/refresh")
    public RefreshResponse refresh(@RequestParam String refreshToken) {
        return authService.refresh(refreshToken);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public void logout(@RequestParam String refreshToken) {
        authService.logout(refreshToken);
    }
}
