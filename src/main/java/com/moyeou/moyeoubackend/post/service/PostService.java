package com.moyeou.moyeoubackend.post.service;

import com.moyeou.moyeoubackend.common.exception.UnAuthorizedException;
import com.moyeou.moyeoubackend.member.domain.Member;
import com.moyeou.moyeoubackend.member.repository.MemberRepository;
import com.moyeou.moyeoubackend.notification.NotificationService;
import com.moyeou.moyeoubackend.notification.domain.*;
import com.moyeou.moyeoubackend.notification.repository.NotificationRepository;
import com.moyeou.moyeoubackend.post.controller.request.*;
import com.moyeou.moyeoubackend.post.controller.response.ItemResponse;
import com.moyeou.moyeoubackend.post.controller.response.PostResponse;
import com.moyeou.moyeoubackend.post.controller.response.PostsResponse;
import com.moyeou.moyeoubackend.post.domain.*;
import com.moyeou.moyeoubackend.post.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final PostQueryRepository postQueryRepository;
    private final MemberRepository memberRepository;
    private final HashtagRepository hashtagRepository;
    private final ItemRepository itemRepository;
    private final ParticipationRepository participationRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @Transactional
    public Long create(CreateRequest request, Long memberId) {
        Member host = findByMemberId(memberId);
        Post post = postRepository.save(request.toEntity(host));
        List<PostHashtag> postHashtags = getPostHashtags(request.getHashtags(), post);
        post.addPostHashtag(postHashtags);
        return post.getId();
    }

    public PostResponse find(Long postId, Long memberId) {
        Member member = findByMemberId(memberId);
        Post post = findByPostId(postId);
        return PostResponse.from(post, post.isHost(member));
    }

    public List<PostsResponse> findAll(String title, Long categoryId, Long hashtagId, PostStatus status, Pageable pageable) {
        return postQueryRepository.findAllByCondition(title, categoryId, hashtagId, status, pageable)
                .stream()
                .map(PostsResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void update(Long postId, Long memberId, UpdateRequest request) {
        Member member = findByMemberId(memberId);
        Post post = findByPostId(postId);
        if (!post.isHost(member)) {
            throw new UnAuthorizedException("작성자만 수정할 수 있습니다.");
        }
        List<PostHashtag> postHashtags = getPostHashtags(request.getHashtags(), post);
        post.update(
                request.getTitle(), request.getHeadCount(),
                request.getOperationWay(), request.getExpectedDate(),
                request.getEstimatedDuration(), request.getContent(),
                postHashtags, request.getItems()
        );
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

    public List<ItemResponse> findForm(Long postId) {
        Post post = findByPostId(postId);
        return post.getItems().stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long attend(Long postId, Long memberId, AttendRequest request) {
        Member member = findByMemberId(memberId);
        Post post = findByPostId(postId);
        Participation participation = post.attend(member);

        List<Answer> answers = request.getAnswers()
                .stream()
                .map(it -> {
                    Item item = findByItemId(it.getItemId());
                    return new Answer(participation, item, it.getAnswer());
                })
                .collect(Collectors.toList());
        participation.addAnswers(answers);
        postRepository.flush();

        Long hostId = post.getHost().getId();
        notificationService.sendMessage(hostId, "신청");
        Notification notification = new Notification(hostId, NotificationType.ATTEND, member, post);
        notificationRepository.save(notification);

        return participation.getId();
    }

    @Transactional
    public void cancel(Long postId, Long memberId) {
        Member member = findByMemberId(memberId);
        Post post = findByPostId(postId);
        post.cancel(member);

        Long hostId = post.getHost().getId();
        notificationService.sendMessage(hostId, "신청 취소");
        Notification notification = new Notification(hostId, NotificationType.CANCEL, member, post);
        notificationRepository.save(notification);
    }

    @Transactional
    public void accept(Long postId, Long participationId, Long memberId) {
        Member member = findByMemberId(memberId);
        Post post = findByPostId(postId);
        Participation participation = findByParticipationId(participationId);
        post.accept(member, participation);

        Long attendId = participation.getMember().getId();
        notificationService.sendMessage(attendId, "신청 수락");
        Notification notification = new Notification(attendId, NotificationType.ACCEPT, post);
        notificationRepository.save(notification);
    }

    @Transactional
    public void reject(Long postId, Long participationId, Long memberId) {
        Member member = findByMemberId(memberId);
        Post post = findByPostId(postId);
        Participation participation = findByParticipationId(participationId);
        post.reject(member, participation);

        Long attendId = participation.getMember().getId();
        notificationService.sendMessage(attendId, "신청 거절");
        Notification notification = new Notification(attendId, NotificationType.REJECT, post);
        notificationRepository.save(notification);
    }

    @Transactional
    public void complete(Long postId, Long memberId) {
        Member member = findByMemberId(memberId);
        Post post = findByPostId(postId);
        post.complete(member);

        for (Participation participation : post.getParticipations()) {
            Long attendId = participation.getMember().getId();
            notificationService.sendMessage(attendId, "모집 완료. 스터디 시작");
            Notification notification = new Notification(attendId, NotificationType.COMPLETE, post);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void end(Long postId, Long memberId) {
        Member member = findByMemberId(memberId);
        Post post = findByPostId(postId);
        post.end(member);

        for (Participation participation : post.getParticipations()) {
            Long attendId = participation.getMember().getId();
            notificationService.sendMessage(attendId, "스터디 종료. 평가ㄱㄱ");
            Notification notification = new Notification(attendId, NotificationType.END, post);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void createComment(Long postId, Long memberId, CommentRequest request) {
        Member member = findByMemberId(memberId);
        Post post = findByPostId(postId);
        post.addComment(member, request.getContent());

        Long hostId = post.getHost().getId();
        notificationService.sendMessage(hostId, "게시물에 댓글 달림");
        Notification notification = new Notification(hostId, NotificationType.COMMENT, post);
        notificationRepository.save(notification);
    }

    @Transactional
    public void updateComment(Long memberId, Long postId, Long commentId, CommentUpdateRequest request) {
        Member member = findByMemberId(memberId);
        Post post = findByPostId(postId);
        post.updateComment(member, commentId, request.getContent());
    }

    @Transactional
    public void deleteComment(Long memberId, Long postId, Long commentId) {
        Member member = findByMemberId(memberId);
        Post post = findByPostId(postId);
        post.removeComment(member, commentId);
    }

    private Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원(memberId: " + memberId + ")이 존재하지 않습니다."));
    }

    private Post findByPostId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시물(postId: " + postId + ")이 존재하지 않습니다."));
    }

    private Item findByItemId(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("항목(itemId: " + itemId + ")이 존재하지 않습니다."));
    }

    private Participation findByParticipationId(Long participationId) {
        return participationRepository.findById(participationId)
                .orElseThrow(() -> new EntityNotFoundException("참여(participationId: " + participationId + ")가 존재하지 않습니다."));
    }

    private List<PostHashtag> getPostHashtags(List<String> hashtags, Post post) {
        if (hashtags == null || hashtags.isEmpty()) {
            return Collections.emptyList();
        }
        List<PostHashtag> postHashtags = new ArrayList<>();
        for (String name : hashtags) {
            Hashtag hashtag = hashtagRepository.findByName(name)
                    .orElseThrow(() -> new EntityNotFoundException("해시태그가 존재하지 않습니다."));
            postHashtags.add(new PostHashtag(post, hashtag));
        }
        return postHashtags;
    }
}
