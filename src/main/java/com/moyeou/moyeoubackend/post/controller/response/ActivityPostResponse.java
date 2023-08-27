package com.moyeou.moyeoubackend.post.controller.response;

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
public class ActivityPostResponse {
    private Long postId;
    private String title;
    private PostStatus status;
    private List<String> hashtags;
    private Boolean isHost;
    private LocalDate date;

    public static ActivityPostResponse from(Post post, Boolean isHost) {
        return new ActivityPostResponse(
                post.getId(),
                post.getTitle(),
                post.getStatus(),
                post.getPostHashtags().stream()
                        .map(postHashtag -> postHashtag.getHashtag().getName())
                        .collect(Collectors.toList()),
                isHost,
                post.getCreatedAt()
        );
    }
}
