package com.moyeou.moyeoubackend.member.controller.response;

import com.moyeou.moyeoubackend.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HostResponse {
    private Long id;
    private String nickname;

    public static HostResponse from(Member member) {
        return new HostResponse(member.getId(), member.getNickname());
    }
}
