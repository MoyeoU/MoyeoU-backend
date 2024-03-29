package com.moyeou.moyeoubackend.member.repository;

import com.moyeou.moyeoubackend.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);
}
