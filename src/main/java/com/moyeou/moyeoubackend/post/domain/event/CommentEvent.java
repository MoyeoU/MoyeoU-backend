package com.moyeou.moyeoubackend.post.domain.event;

import lombok.Getter;

@Getter
public class CommentEvent {
    private Long postId;

    public CommentEvent(Long postId) {
        this.postId = postId;
    }
}
