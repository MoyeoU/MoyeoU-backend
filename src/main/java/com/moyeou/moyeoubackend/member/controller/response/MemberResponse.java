package com.moyeou.moyeoubackend.member.controller.response;

import com.moyeou.moyeoubackend.member.domain.Member;
import com.moyeou.moyeoubackend.post.controller.response.ActivityPostResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

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
    private List<String> hashtags;
    private List<ActivityPostResponse> activityList;

    public static MemberResponse from(Member member, List<ActivityPostResponse> posts) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getImagePath(),
                member.getPoint(),
                member.getIntroduction(),
                member.getMemberHashtags().stream()
                        .map(hashtag -> hashtag.getHashtag().getName())
                        .collect(Collectors.toList()),
                posts
        );
    }
}
