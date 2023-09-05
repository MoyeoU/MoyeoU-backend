package com.moyeou.moyeoubackend.post.domain.event;

import lombok.Getter;

@Getter
public class EndEvent {
    private Long postId;

    public EndEvent(Long postId) {
        this.postId = postId;
    }
}
