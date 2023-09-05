package com.moyeou.moyeoubackend.post.domain.event;

import lombok.Getter;

@Getter
public class CancelEvent {
    private Long memberId;
    private Long postId;

    public CancelEvent(Long memberId, Long postId) {
        this.memberId = memberId;
        this.postId = postId;
    }
}
