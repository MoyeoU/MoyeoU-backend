package com.moyeou.moyeoubackend.post.controller.request;

import com.moyeou.moyeoubackend.post.domain.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostsRequest {
    private String title;
    private Long categoryId;
    private Long hashTagId;
    private String status;

    public PostStatus getStatus() {
        return PostStatus.matchStatus(status);
    }
}
