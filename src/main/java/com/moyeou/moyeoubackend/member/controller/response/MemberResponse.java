package com.moyeou.moyeoubackend.member.controller.response;

import com.moyeou.moyeoubackend.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    private Long id;
    private String email;
    private String nickname;
    private String imagePath;
    private Double point;
    private String introduction;

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getImagePath(),
                member.getPoint(),
                member.getIntroduction()
        );
    }
}
