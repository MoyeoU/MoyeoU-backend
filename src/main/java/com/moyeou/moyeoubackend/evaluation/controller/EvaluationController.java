package com.moyeou.moyeoubackend.evaluation.controller;

import com.moyeou.moyeoubackend.auth.supports.LoginMember;
import com.moyeou.moyeoubackend.evaluation.controller.request.EvaluationRequest;
import com.moyeou.moyeoubackend.evaluation.controller.response.EvaluateeResponse;
import com.moyeou.moyeoubackend.evaluation.controller.response.EvaluationResponse;
import com.moyeou.moyeoubackend.evaluation.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Evaluation", description = "평가 관련 API")
public class EvaluationController {
    private final EvaluationService evaluationService;

    @Operation(summary = "평가할 목록 조회")
    @GetMapping("/posts/{postId}/evaluations")
    public List<EvaluateeResponse> findEvaluatee(@PathVariable Long postId, @LoginMember Long memberId) {
        return evaluationService.findEvaluatee(postId, memberId);
    }

    @Operation(summary = "평가된 목록 조회")
    @GetMapping("/members/{memberId}/evaluations")
    public List<EvaluationResponse> findEvaluation(@PathVariable Long memberId) {
        return evaluationService.findEvaluation(memberId);
    }

    @Operation(summary = "평가하기")
    @PostMapping("/posts/{postId}/evaluations/{evaluationId}")
    public void evaluate(@PathVariable Long evaluationId, @RequestBody EvaluationRequest request) {
        evaluationService.evaluate(evaluationId, request);
    }
}
