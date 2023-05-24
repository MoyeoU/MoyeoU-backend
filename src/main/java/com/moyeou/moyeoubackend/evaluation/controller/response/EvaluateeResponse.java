package com.moyeou.moyeoubackend.evaluation.controller.response;

import com.moyeou.moyeoubackend.evaluation.domain.Evaluation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluateeResponse {
    private Long id;
    private EvaluateeMemberResponse member;
    private Boolean evaluated;

    public static EvaluateeResponse from(Evaluation evaluation) {
        return new EvaluateeResponse(
                evaluation.getId(),
                EvaluateeMemberResponse.from(evaluation.getEvaluatee()),
                evaluation.getEvaluated()
        );
    }
}
