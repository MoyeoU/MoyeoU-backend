package com.moyeou.moyeoubackend.post.controller.request;

import com.moyeou.moyeoubackend.member.domain.Member;
import com.moyeou.moyeoubackend.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static com.moyeou.moyeoubackend.post.domain.PostStatus.PROGRESS;

@Getter
@Builder
@AllArgsConstructor
public class CreateRequest {
    private String title;
    private Integer headCount;
    private String operationWay;
    private String expectedDate;
    private String estimatedDuration;
    private String content;
    private List<String> hashtags;

    public Post toEntity(Member host) {
        return Post.builder()
                .title(title)
                .headCount(headCount)
                .currentCount(1)
                .operationWay(operationWay)
                .expectedDate(expectedDate)
                .estimatedDuration(estimatedDuration)
                .content(content)
                .status(PROGRESS)
                .host(host)
                .postHashtags(new ArrayList<>())
                .build();
    }
}
