package com.moyeou.moyeoubackend.post.domain.event;

import lombok.Getter;

@Getter
public class AttendEvent {
    private Long memberId;
    private Long postId;
    private Long participationId;

    public AttendEvent(Long memberId, Long postId, Long participationId) {
        this.memberId = memberId;
        this.postId = postId;
        this.participationId = participationId;
    }
}
