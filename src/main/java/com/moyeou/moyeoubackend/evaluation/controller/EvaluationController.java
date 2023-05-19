package com.moyeou.moyeoubackend.evaluation.controller;

import com.moyeou.moyeoubackend.auth.supports.LoginMember;
import com.moyeou.moyeoubackend.evaluation.controller.request.EvaluationRequest;
import com.moyeou.moyeoubackend.evaluation.controller.response.EvaluateeResponse;
import com.moyeou.moyeoubackend.evaluation.controller.response.EvaluationResponse;
import com.moyeou.moyeoubackend.evaluation.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EvaluationController {
    private final EvaluationService evaluationService;

    @GetMapping("/posts/{postId}/evaluations")
    public List<EvaluateeResponse> findEvaluatee(@PathVariable Long postId, @LoginMember Long memberId) {
        return evaluationService.findEvaluatee(postId, memberId);
    }

    @GetMapping("/members/{memberId}/evaluations")
    public List<EvaluationResponse> findEvaluation(@PathVariable Long memberId) {
        return evaluationService.findEvaluation(memberId);
    }

    @PostMapping("/posts/{postId}/evaluations/{evaluationId}")
    public void evaluate(@PathVariable Long evaluationId, @RequestBody EvaluationRequest request) {
        evaluationService.evaluate(evaluationId, request);
    }
}
