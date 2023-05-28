package com.moyeou.moyeoubackend.post.controller.response;

import com.moyeou.moyeoubackend.member.controller.response.HostResponse;
import com.moyeou.moyeoubackend.post.domain.Post;
import com.moyeou.moyeoubackend.post.domain.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private String title;
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
    private List<CommentResponse> comments;

    public static PostResponse from(Post post, Boolean isHost) {
        return new PostResponse(
                post.getTitle(),
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
                isHost,
                post.getComments().stream()
                        .map(CommentResponse::from)
                        .collect(Collectors.toList())
        );
    }
}
