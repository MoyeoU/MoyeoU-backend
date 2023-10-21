package com.moyeou.moyeoubackend.post.domain.event;

import lombok.Getter;

@Getter
public class AttendEvent {
    private Long memberId;
    private Long postId;

    public AttendEvent(Long memberId, Long postId) {
        this.memberId = memberId;
        this.postId = postId;
    }
}
