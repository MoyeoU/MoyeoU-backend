package com.moyeou.moyeoubackend.member.service;

import com.moyeou.moyeoubackend.member.controller.request.SignUpRequest;
import com.moyeou.moyeoubackend.member.exception.DuplicateMemberException;
import com.moyeou.moyeoubackend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long save(SignUpRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateMemberException();
        }
        String password = passwordEncoder.encode(request.getPassword());
        return memberRepository.save(request.toEntity(password)).getId();
    }
}
