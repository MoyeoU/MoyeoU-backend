package com.moyeou.moyeoubackend.auth.controller;

import com.moyeou.moyeoubackend.auth.controller.request.RefreshRequest;
import com.moyeou.moyeoubackend.auth.controller.request.LoginRequest;
import com.moyeou.moyeoubackend.auth.controller.response.LoginResponse;
import com.moyeou.moyeoubackend.auth.controller.response.RefreshResponse;
import com.moyeou.moyeoubackend.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        RefreshResponse refreshResponse = authService.refresh(refreshToken);
        return ResponseEntity.ok(refreshResponse);
    }
}
