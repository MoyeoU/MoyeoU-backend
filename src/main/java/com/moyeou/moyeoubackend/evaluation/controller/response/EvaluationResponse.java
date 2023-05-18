package com.moyeou.moyeoubackend.evaluation.controller.response;

import com.moyeou.moyeoubackend.evaluation.domain.Evaluation;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EvaluationResponse {
    private Long id;
    private Double point;
    private String content;

    public EvaluationResponse(Double point, String content) {
        this.point = point;
        this.content = content;
    }

    public static EvaluationResponse from(Evaluation evaluation) {
        return new EvaluationResponse(evaluation.getPoint(), evaluation.getContent());
    }
}
