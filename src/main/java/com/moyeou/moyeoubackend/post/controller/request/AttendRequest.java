package com.moyeou.moyeoubackend.post.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AttendRequest {
    private List<AnswerRequest> answers;

    public AttendRequest(List<AnswerRequest> answers) {
        this.answers = answers;
    }
}
