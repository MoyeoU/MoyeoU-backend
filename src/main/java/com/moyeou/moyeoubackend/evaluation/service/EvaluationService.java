package com.moyeou.moyeoubackend.evaluation.service;

import com.moyeou.moyeoubackend.evaluation.controller.request.EvaluationRequest;
import com.moyeou.moyeoubackend.evaluation.controller.response.EvaluateeResponse;
import com.moyeou.moyeoubackend.evaluation.controller.response.EvaluationResponse;
import com.moyeou.moyeoubackend.evaluation.domain.Evaluation;
import com.moyeou.moyeoubackend.evaluation.repository.EvaluationRepository;
import com.moyeou.moyeoubackend.member.domain.Member;
import com.moyeou.moyeoubackend.member.repository.MemberRepository;
import com.moyeou.moyeoubackend.post.domain.Post;
import com.moyeou.moyeoubackend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EvaluationService {
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final EvaluationRepository evaluationRepository;

    public List<EvaluateeResponse> findEvaluatee(Long postId, Long memberId) {
        Member member = findByMemberId(memberId);
        Post post = findByPostId(postId);
        return post.getEvaluations().stream()
                .filter(evaluation -> evaluation.isEvaluator(member))
                .map(EvaluateeResponse::from)
                .collect(Collectors.toList());
    }

    public List<EvaluationResponse> findEvaluation(Long memberId) {
        List<Evaluation> evaluations = evaluationRepository.findAllByEvaluateeId(memberId);
        return evaluations.stream()
                .map(EvaluationResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void evaluate(Long evaluationId, EvaluationRequest request) {
        Evaluation evaluation = findByEvaluationId(evaluationId);
        evaluation.evaluate(request.getPoint(), request.getContent());
    }

    private Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원(memberId: " + memberId + ")이 존재하지 않습니다."));
    }

    private Post findByPostId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시물(postId: " + postId + ")이 존재하지 않습니다."));
    }

    private Evaluation findByEvaluationId(Long evaluationId) {
        return evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new EntityNotFoundException("평가(evaluationId: " + evaluationId + ")이 존재하지 않습니다."));
    }
}
