package com.moyeou.moyeoubackend.member.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateRequest {
    private String introduction;

    @NotEmpty(message = "닉네임을 입력해주세요")
    private String nickname;

    private String imagePath;

    private List<String> hashtags;
}
