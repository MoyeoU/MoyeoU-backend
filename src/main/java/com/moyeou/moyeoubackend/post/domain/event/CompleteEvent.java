package com.moyeou.moyeoubackend.post.domain.event;

import lombok.Getter;

@Getter
public class CompleteEvent {
    private Long postId;

    public CompleteEvent(Long postId) {
        this.postId = postId;
    }
}
