package com.moyeou.moyeoubackend.evaluation.repository;

import com.moyeou.moyeoubackend.evaluation.domain.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findAllByEvaluateeId(Long memberId);
}
