package com.moyeou.moyeoubackend.evaluation.service;

import com.moyeou.moyeoubackend.IntegrationTest;
import com.moyeou.moyeoubackend.evaluation.controller.request.EvaluationRequest;
import com.moyeou.moyeoubackend.evaluation.controller.response.EvaluationResponse;
import com.moyeou.moyeoubackend.evaluation.domain.Evaluation;
import com.moyeou.moyeoubackend.evaluation.repository.EvaluationRepository;
import com.moyeou.moyeoubackend.member.domain.Member;
import com.moyeou.moyeoubackend.member.repository.MemberRepository;
import com.moyeou.moyeoubackend.post.domain.Post;
import com.moyeou.moyeoubackend.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class EvaluationServiceTest extends IntegrationTest {
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private EvaluationRepository evaluationRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;

    @DisplayName("평가를 한다")
    @Test
    void evaluate() {
        Member member1 = saveMember("member1@o.cnu.ac.kr");
        Member member2 = saveMember("member2@o.cnu.ac.kr");
        Member member3 = saveMember("member3@o.cnu.ac.kr");
        Post post = savePost(member1);

        Evaluation evaluation1 = evaluationRepository.save(new Evaluation(member1, member2, post));
        Evaluation evaluation2 = evaluationRepository.save(new Evaluation(member3, member2, post));

        // member1, member3이 member2를 평가
        evaluationService.evaluate(evaluation1.getId(), new EvaluationRequest(5.0, "성실해요"));
        evaluationService.evaluate(evaluation2.getId(), new EvaluationRequest(4.0, "good"));

        // member2 평가 내역 조회
        List<EvaluationResponse> evaluationList = evaluationService.findEvaluation(member2.getId());

        assertAll(
                () -> assertThat(member2.getPoint()).isEqualTo(4.5),
                () -> assertThat(evaluationList.size()).isEqualTo(2),
                () -> assertThat(evaluationList.get(0).getPoint()).isEqualTo(5.0),
                () -> assertThat(evaluationList.get(0).getContent()).isEqualTo("성실해요"),
                () -> assertThat(evaluationList.get(1).getPoint()).isEqualTo(4.0),
                () -> assertThat(evaluationList.get(1).getContent()).isEqualTo("good")
        );
    }

    private Member saveMember(String email) {
        return memberRepository.save(new Member(email, "컴퓨터융합학부", "nickname", "pw"));
    }

    private Post savePost(Member host) {
        return postRepository.save(Post.builder()
                .title("JPA 스터디").headCount(3).operationWay("대면")
                .expectedDate("06-01").estimatedDuration("3개월").content("<h1>같이 공부해요!</h1>")
                .host(host).items(new ArrayList<>())
                .build());
    }
}