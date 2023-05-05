package com.moyeou.moyeoubackend.member.service;

import com.moyeou.moyeoubackend.member.controller.request.SignUpRequest;
import com.moyeou.moyeoubackend.member.controller.request.UpdateRequest;
import com.moyeou.moyeoubackend.file.service.FileSystemStorageService;
import com.moyeou.moyeoubackend.member.domain.Member;
import com.moyeou.moyeoubackend.member.exception.DuplicateMemberException;
import com.moyeou.moyeoubackend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileSystemStorageService fileUploader;

    @Transactional
    public Long save(SignUpRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateMemberException();
        }
        String password = passwordEncoder.encode(request.getPassword());
        return memberRepository.save(request.toEntity(password)).getId();
    }

    @Transactional
    public void update(Long memberId, UpdateRequest request) {
        Member member = findById(memberId);
        String path = fileUploader.upload(request.getFile());
        member.update(request.getIntroduction(), request.getNickname(), path);
    }

    @Transactional
    public void delete(Long memberId) {
        memberRepository.deleteById(memberId);
    }

    private Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원(memberId: " + memberId + ")이 존재하지 않습니다."));
    }
}
