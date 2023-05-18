package com.moyeou.moyeoubackend.evaluation.controller.response;

import com.moyeou.moyeoubackend.evaluation.domain.Evaluation;
import com.moyeou.moyeoubackend.member.controller.response.MemberResponse;
import com.moyeou.moyeoubackend.member.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EvaluateeResponse {
    private Long id;
    private MemberResponse member;
    private Boolean evaluated;

    public EvaluateeResponse(MemberResponse response, Boolean evaluated) {
        this.member = response;
        this.evaluated = evaluated;
    }

    public static EvaluateeResponse from(Evaluation evaluation) {
        Member evaluatee = evaluation.getEvaluatee();
        Boolean evaluated = evaluation.getEvaluated();
        return new EvaluateeResponse(MemberResponse.from(evaluatee), evaluated);
    }
}
