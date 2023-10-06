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
public class PostsResponse {
    private Long postId;
    private String title;
    private PostStatus status;
    private Integer headCount;
    private Integer currentCount;
    private List<String> hashtags;
    private HostResponse host;

    public static PostsResponse from(Post post) {
        return new PostsResponse(
                post.getId(),
                post.getTitle(),
                post.getStatus(),
                post.getHeadCount(),
                post.getCurrentCount(),
                post.getPostHashtags().stream()
                        .map(postHashtag -> postHashtag.getHashtag().getName())
                        .collect(Collectors.toList()),
                HostResponse.from(post.getHost())
        );
    }
}
