package com.moyeou.moyeoubackend.evaluation.repository;

import com.moyeou.moyeoubackend.evaluation.domain.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
}
