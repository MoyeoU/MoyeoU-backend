package com.moyeou.moyeoubackend.evaluation.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequest {
    @Min(0)
    @NotNull(message = "점수를 입력해주세요")
    private Double point;

    @NotEmpty(message = "내용을 입력해주세요")
    private String content;
}
