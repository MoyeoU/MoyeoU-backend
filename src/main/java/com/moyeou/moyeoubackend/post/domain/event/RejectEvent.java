package com.moyeou.moyeoubackend.post.domain.event;

import lombok.Getter;

@Getter
public class RejectEvent {
    private Long postId;
    private Long participationId;

    public RejectEvent(Long postId, Long participationId) {
        this.postId = postId;
        this.participationId = participationId;
    }
}
