package com.moyeou.moyeoubackend.auth.controller.response;

import com.moyeou.moyeoubackend.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String nickname;
    private String accessToken;
    private String refreshToken;

    public static LoginResponse from(Member member, String accessToken, String refreshToken) {
        return new LoginResponse(
                member.getNickname(),
                accessToken,
                refreshToken
        );
    }
}
