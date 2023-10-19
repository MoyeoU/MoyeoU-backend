package com.moyeou.moyeoubackend.post.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponse {
    private String itemName;
    private String answer;

    public static AnswerResponse from(String itemName, String answer) {
        return new AnswerResponse(itemName, answer);
    }
}
