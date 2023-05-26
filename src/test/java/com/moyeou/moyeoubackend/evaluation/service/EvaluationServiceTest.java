package com.moyeou.moyeoubackend.evaluation.service;

import com.moyeou.moyeoubackend.evaluation.controller.request.EvaluationRequest;
import com.moyeou.moyeoubackend.evaluation.domain.Evaluation;
import com.moyeou.moyeoubackend.evaluation.repository.EvaluationRepository;
import com.moyeou.moyeoubackend.member.domain.Member;
import com.moyeou.moyeoubackend.member.repository.MemberRepository;
import com.moyeou.moyeoubackend.post.domain.Post;
import com.moyeou.moyeoubackend.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@SpringBootTest
public class EvaluationServiceTest {
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
        Post post = savePost(member1);
        Evaluation evaluation = evaluationRepository.save(new Evaluation(member1, member2, post));

        evaluationService.evaluate(evaluation.getId(), new EvaluationRequest(5.0, "좋아요"));

        assertAll(
                () -> assertThat(evaluation.getPoint()).isEqualTo(5.0),
                () -> assertThat(evaluation.getContent()).isEqualTo("좋아요"),
                () -> assertThat(evaluation.getEvaluated()).isTrue(),
                () -> assertThat(evaluation.getEvaluatee()).isEqualTo(member2)
        );
    }

    private Member saveMember(String email) {
        return memberRepository.save(new Member(email, "컴퓨터융합학부", 202000000, "nickname", "pw"));
    }

    private Post savePost(Member host) {
        return postRepository.save(Post.builder()
                .title("JPA 스터디").headCount(3).operationWay("대면")
                .expectedDate("06-01").estimatedDuration("3개월").content("<h1>같이 공부해요!</h1>")
                .host(host).items(new ArrayList<>())
                .build());
    }
}
