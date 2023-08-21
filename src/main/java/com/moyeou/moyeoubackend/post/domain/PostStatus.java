package com.moyeou.moyeoubackend.post.domain;

import java.util.Arrays;

public enum PostStatus {
    PROGRESS, COMPLETED, END;

    public static PostStatus matchStatus(String value) {
        return Arrays.stream(PostStatus.values())
                .filter(status -> status.name().equals(value))
                .findAny()
                .orElseThrow();
    }
}
