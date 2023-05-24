package com.moyeou.moyeoubackend.post.repository;

import com.moyeou.moyeoubackend.post.domain.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findAllByMemberId(Long memberId);
}
