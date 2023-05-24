package com.moyeou.moyeoubackend.evaluation.controller.response;

import com.moyeou.moyeoubackend.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluateeMemberResponse {
    private Long id;
    private String nickname;
    private String imagePath;

    public static EvaluateeMemberResponse from(Member member) {
        return new EvaluateeMemberResponse(member.getId(), member.getNickname(), member.getImagePath());
    }
}
