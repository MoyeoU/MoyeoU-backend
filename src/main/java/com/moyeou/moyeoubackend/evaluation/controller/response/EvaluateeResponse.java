package com.moyeou.moyeoubackend.evaluation.controller.response;

import com.moyeou.moyeoubackend.evaluation.domain.Evaluation;
import com.moyeou.moyeoubackend.member.controller.response.MemberResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluateeResponse {
    private Long id;
    private MemberResponse member;
    private Boolean evaluated;

    public static EvaluateeResponse from(Evaluation evaluation) {
        return new EvaluateeResponse(
                evaluation.getId(),
                MemberResponse.from(evaluation.getEvaluatee()),
                evaluation.getEvaluated()
        );
    }
}
