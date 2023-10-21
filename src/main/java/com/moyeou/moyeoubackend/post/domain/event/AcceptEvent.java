package com.moyeou.moyeoubackend.post.domain.event;

import lombok.Getter;

@Getter
public class AcceptEvent {
    private Long memberId;
    private Long postId;

    public AcceptEvent(Long memberId, Long postId) {
        this.memberId = memberId;
        this.postId = postId;
    }
}
