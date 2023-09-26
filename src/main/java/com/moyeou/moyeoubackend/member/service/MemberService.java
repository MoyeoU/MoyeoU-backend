package com.moyeou.moyeoubackend.member.service;

import com.moyeou.moyeoubackend.member.controller.request.SignUpRequest;
import com.moyeou.moyeoubackend.member.controller.request.MemberUpdateRequest;
import com.moyeou.moyeoubackend.file.service.FileSystemStorageService;
import com.moyeou.moyeoubackend.member.controller.response.MemberResponse;
import com.moyeou.moyeoubackend.member.domain.Member;
import com.moyeou.moyeoubackend.member.domain.MemberHashtag;
import com.moyeou.moyeoubackend.member.exception.DuplicateMemberException;
import com.moyeou.moyeoubackend.member.repository.MemberRepository;
import com.moyeou.moyeoubackend.post.controller.response.ActivityPostResponse;
import com.moyeou.moyeoubackend.post.domain.Hashtag;
import com.moyeou.moyeoubackend.post.domain.Participation;
import com.moyeou.moyeoubackend.post.repository.HashtagRepository;
import com.moyeou.moyeoubackend.post.repository.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.moyeou.moyeoubackend.post.domain.PostStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileSystemStorageService fileUploader;
    private final HashtagRepository hashtagRepository;
    private final ParticipationRepository participationRepository;

    @Transactional
    public Long save(SignUpRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateMemberException();
        }
        String password = passwordEncoder.encode(request.getPassword());
        return memberRepository.save(request.toEntity(password)).getId();
    }

    public MemberResponse find(Long memberId) {
        Member member = findById(memberId);
        List<ActivityPostResponse> memberParticipations = participationRepository.findAllByMemberId(memberId).stream()
                .map(Participation::getPost)
                .filter(post -> post.getStatus() == END || post.getStatus() == COMPLETED)
                .map(post -> ActivityPostResponse.from(post, post.isHost(member)))
                .collect(Collectors.toList());
        return MemberResponse.from(member, memberParticipations);
    }

    @Transactional
    public void update(Long memberId, MemberUpdateRequest request) {
        Member member = findById(memberId);
        String path = fileUploader.upload(request.getFile());
        List<MemberHashtag> memberHashtags = getMemberHashtags(request.getHashtags(), member);
        member.update(request.getIntroduction(), request.getNickname(), path, memberHashtags);
    }

    @Transactional
    public void delete(Long memberId) {
        memberRepository.deleteById(memberId);
    }

    private Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원(memberId: " + memberId + ")이 존재하지 않습니다."));
    }

    private List<MemberHashtag> getMemberHashtags(List<String> hashtags, Member member) {
        if (hashtags == null || hashtags.isEmpty()) {
            return Collections.emptyList();
        }

        List<MemberHashtag> memberHashtag = new ArrayList<>();
        for (String name : hashtags) {
            Hashtag hashtag = hashtagRepository.findByName(name)
                    .orElseThrow(() -> new EntityNotFoundException("해시태그가 존재하지 않습니다."));
            memberHashtag.add(new MemberHashtag(member, hashtag));
        }

        return memberHashtag;
    }
}
