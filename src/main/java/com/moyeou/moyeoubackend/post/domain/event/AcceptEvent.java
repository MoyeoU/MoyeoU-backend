package com.moyeou.moyeoubackend.post.domain.event;

import lombok.Getter;

@Getter
public class AcceptEvent {
    private Long postId;
    private Long participationId;

    public AcceptEvent(Long postId, Long participationId) {
        this.postId = postId;
        this.participationId = participationId;
    }
}
