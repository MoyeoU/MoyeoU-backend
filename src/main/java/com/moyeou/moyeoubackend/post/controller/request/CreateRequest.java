package com.moyeou.moyeoubackend.post.controller.request;

import com.moyeou.moyeoubackend.member.domain.Member;
import com.moyeou.moyeoubackend.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRequest {
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

    private List<String> items;

    public Post toEntity(Member host) {
        return Post.builder()
                .title(title)
                .headCount(headCount)
                .operationWay(operationWay)
                .expectedDate(expectedDate)
                .estimatedDuration(estimatedDuration)
                .content(content)
                .host(host)
                .items(items)
                .build();
    }
}
