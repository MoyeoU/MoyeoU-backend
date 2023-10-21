package com.moyeou.moyeoubackend.post.controller.response;

import com.moyeou.moyeoubackend.member.controller.response.HostResponse;
import com.moyeou.moyeoubackend.member.domain.Member;
import com.moyeou.moyeoubackend.post.domain.Post;
import com.moyeou.moyeoubackend.post.domain.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private String title;
    private LocalDate createdAt;
    private Integer headCount;
    private Integer currentCount;
    private String operationWay;
    private String expectedDate;
    private String estimatedDuration;
    private PostStatus status;
    private List<String> hashtags;
    private HostResponse host;
    private String content;
    private Boolean isHost;
    private Boolean attended;
    private List<CommentResponse> comments;

    public static PostResponse from(Post post, Member viewer) {
        return new PostResponse(
                post.getTitle(),
                post.getCreatedAt(),
                post.getHeadCount(),
                post.getCurrentCount(),
                post.getOperationWay(),
                post.getExpectedDate(),
                post.getEstimatedDuration(),
                post.getStatus(),
                post.getPostHashtags().stream()
                        .map(postHashtag -> postHashtag.getHashtag().getName())
                        .collect(Collectors.toList()),
                HostResponse.from(post.getHost()),
                post.getContent(),
                post.isHost(viewer),
                post.getParticipations().stream()
                        .anyMatch(p -> p.getMember().equals(viewer)),
                post.getComments().stream()
                        .map(Comment -> CommentResponse.from(Comment, viewer))
                        .collect(Collectors.toList())
        );
    }
}
