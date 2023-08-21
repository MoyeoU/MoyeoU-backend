package com.moyeou.moyeoubackend.member.controller.request;

import com.moyeou.moyeoubackend.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    @NotEmpty(message = "이메일을 입력해주세요")
    private String email;

    @NotEmpty(message = "학과를 입력해주세요")
    private String department;

    @NotEmpty(message = "닉네임을 입력해주세요")
    private String nickname;

    @NotEmpty(message = "비밀번호를 입력해주세요")
    private String password;

    public Member toEntity(String password) {
        return new Member(email, department, nickname, password);
    }
}
