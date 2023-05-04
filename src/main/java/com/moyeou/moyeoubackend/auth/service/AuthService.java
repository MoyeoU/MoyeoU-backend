package com.moyeou.moyeoubackend.auth.service;

import com.moyeou.moyeoubackend.auth.supports.JwtTokenProvider;
import com.moyeou.moyeoubackend.auth.controller.request.LoginRequest;
import com.moyeou.moyeoubackend.auth.controller.response.LoginResponse;
import com.moyeou.moyeoubackend.auth.controller.response.RefreshResponse;
import com.moyeou.moyeoubackend.auth.entity.RefreshToken;
import com.moyeou.moyeoubackend.auth.exception.IncorrectPasswordException;
import com.moyeou.moyeoubackend.auth.exception.NonExistentEmailException;
import com.moyeou.moyeoubackend.auth.exception.UnauthenticatedException;
import com.moyeou.moyeoubackend.auth.repository.RefreshTokenRepository;
import com.moyeou.moyeoubackend.member.domain.Member;
import com.moyeou.moyeoubackend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest loginRequest) {
        Member member = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(NonExistentEmailException::new);

        if (passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            String accessToken = jwtTokenProvider.createAccessToken(member);
            String refreshToken = jwtTokenProvider.createRefreshToken(member);
            refreshTokenRepository.save(new RefreshToken(refreshToken));
            return LoginResponse.from(member, accessToken, refreshToken);
        }
        throw new IncorrectPasswordException();
    }

    public RefreshResponse refresh(String token) {
        if (reissueable(token)) {
            refreshTokenRepository.deleteByRefreshToken(token);
            Long memberId = jwtTokenProvider.extractMemberIdFromRefreshToken(token);
            Member member = memberRepository.findById(memberId)
                    .orElseThrow();
            String accessToken = jwtTokenProvider.createAccessToken(member);
            String refreshToken = jwtTokenProvider.createRefreshToken(member);
            refreshTokenRepository.save(new RefreshToken(refreshToken));
            return new RefreshResponse(accessToken, refreshToken);
        }
        throw new UnauthenticatedException("다시 로그인 해주세요.");
    }

    private boolean reissueable(String token) {
        return jwtTokenProvider.validateRefreshToken(token)
                && refreshTokenRepository.existsByRefreshToken(token);
    }
}
