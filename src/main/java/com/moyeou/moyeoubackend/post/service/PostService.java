package com.moyeou.moyeoubackend.post.service;

import com.moyeou.moyeoubackend.common.exception.UnAuthorizedException;
import com.moyeou.moyeoubackend.member.domain.Member;
import com.moyeou.moyeoubackend.member.repository.MemberRepository;
import com.moyeou.moyeoubackend.post.controller.request.CreateRequest;
import com.moyeou.moyeoubackend.post.controller.request.UpdateRequest;
import com.moyeou.moyeoubackend.post.controller.response.PostResponse;
import com.moyeou.moyeoubackend.post.domain.*;
import com.moyeou.moyeoubackend.post.repository.HashtagRepository;
import com.moyeou.moyeoubackend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final HashtagRepository hashtagRepository;

    @Transactional
    public Long create(CreateRequest request, Long memberId) {
        Member host = findByMemberId(memberId);
        Post post = postRepository.save(request.toEntity(host));
        List<PostHashtag> postHashtags = getPostHashtags(request.getHashtags(), post);
        post.setPostHashtag(postHashtags);
        post.addParticipation(host);
        return post.getId();
    }

    public PostResponse find(Long postId, Long memberId) {
        Member member = findByMemberId(memberId);
        Post post = findByPostId(postId);
        if (post.isHost(member)) {
            return PostResponse.from(post, true);
        }
        return PostResponse.from(post, false);
    }

    @Transactional
    public void update(Long postId, Long memberId, UpdateRequest request) {
        Member member = findByMemberId(memberId);
        Post post = findByPostId(postId);
        if (!post.isHost(member)) {
            throw new UnAuthorizedException("작성자만 삭제할 수 있습니다.");
        }
        List<PostHashtag> postHashtags = getPostHashtags(request.getHashtags(), post);
        post.update(request.getTitle(), request.getHeadCount(), request.getOperationWay(), request.getExpectedDate(),
                request.getEstimatedDuration(), request.getContent(), postHashtags);
    }

    @Transactional
    public void delete(Long postId, Long memberId) {
        Member host = findByMemberId(memberId);
        Post post = findByPostId(postId);
        if (!post.isHost(host)) {
            throw new UnAuthorizedException("작성자만 삭제할 수 있습니다.");
        }
        postRepository.deleteById(post.getId());
    }

    @Transactional
    public Long attend(Long postId, Long memberId) {
        Member member = findByMemberId(memberId);
        Post post = findByPostId(postId);
        Participation participation = post.attend(member);
        postRepository.flush();
        return participation.getId();
    }

    @Transactional
    public void cancel(Long postId, Long memberId) {
        Member member = findByMemberId(memberId);
        Post post = findByPostId(postId);
        post.cancel(member);
    }

    @Transactional
    public void complete(Long postId, Long memberId) {
        Member member = findByMemberId(memberId);
        Post post = findByPostId(postId);
        if (!post.isHost(member)) {
            throw new UnAuthorizedException("작성자만 모집을 완료할 수 있습니다.");
        }
        post.complete();
    }

    @Transactional
    public void end(Long postId, Long memberId) {
        Member member = findByMemberId(memberId);
        Post post = findByPostId(postId);
        if (!post.isHost(member)) {
            throw new UnAuthorizedException("작성자만 종료할 수 있습니다.");
        }
        post.end();
    }

    private Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원(memberId: " + memberId + ")이 존재하지 않습니다."));
    }

    private Post findByPostId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시물(postId: " + postId + ")이 존재하지 않습니다."));
    }

    private List<PostHashtag> getPostHashtags(List<String> hashtags, Post post) {
        List<PostHashtag> postHashtags = new ArrayList<>();
        for (String name : hashtags) {
            Hashtag hashtag = hashtagRepository.findByName(name)
                    .orElseThrow(() -> new EntityNotFoundException("해시태그가 존재하지 않습니다."));
            postHashtags.add(new PostHashtag(post, hashtag));
        }
        return postHashtags;
    }
}
