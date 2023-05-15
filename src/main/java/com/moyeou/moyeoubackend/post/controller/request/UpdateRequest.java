package com.moyeou.moyeoubackend.post.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UpdateRequest {
    @NotEmpty(message = "제목을 입력해주세요")
    private String title;

    @Min(2)
    @NotNull(message = "모집 인원을 입력해주세요")
    private Integer headCount;

    @NotEmpty(message = "운영 방식을 입력해주세요")
    private String operationWay;

    @NotEmpty(message = "예상 시작일을 입력해주세요")
    private String expectedDate;

    @NotEmpty(message = "예상 기간을 입력해주세요")
    private String estimatedDuration;

    @NotEmpty(message = "내용을 입력해주세요")
    private String content;

    private List<String> hashtags;

}
