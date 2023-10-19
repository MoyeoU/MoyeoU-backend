package com.moyeou.moyeoubackend.post.domain.event;

import lombok.Getter;

@Getter
public class RejectEvent {
    private Long memberId;
    private Long postId;

    public RejectEvent(Long memberId, Long postId) {
        this.memberId = memberId;
        this.postId = postId;
    }
}
