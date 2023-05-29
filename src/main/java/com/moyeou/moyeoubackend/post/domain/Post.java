package com.moyeou.moyeoubackend.post.domain;

import com.moyeou.moyeoubackend.common.exception.UnAuthorizedException;
import com.moyeou.moyeoubackend.evaluation.domain.Evaluation;
import com.moyeou.moyeoubackend.member.domain.Member;
import com.moyeou.moyeoubackend.post.exception.NonExistentItemException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.moyeou.moyeoubackend.post.domain.PostStatus.*;
import static com.moyeou.moyeoubackend.post.domain.PostStatus.COMPLETED;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "post")
@NoArgsConstructor(access = PROTECTED)
public class Post {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "head_count", nullable = false)
    private Integer headCount;

    @Column(name = "current_count", nullable = false)
    private Integer currentCount;

    @Column(name = "operation_way", nullable = false)
    private String operationWay;

    @Column(name = "expected_date", nullable = false)
    private String expectedDate;

    @Column(name = "estimated_duration", nullable = false)
    private String estimatedDuration;

    @Column(name = "content", nullable = false)
    private String content;

    @Enumerated(value = STRING)
    @Column(name = "status", nullable = false)
    private PostStatus status;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member host;

    @OneToMany(mappedBy = "post", cascade = ALL, orphanRemoval = true)
    private List<Participation> participations = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = ALL, orphanRemoval = true)
    private List<PostHashtag> postHashtags = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = ALL, orphanRemoval = true)
    private List<Evaluation> evaluations = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Post(String title, Integer headCount, String operationWay, String expectedDate,
                String estimatedDuration, String content, Member host, List<String> items) {
        if (items == null) {
            throw new NonExistentItemException();
        }
        this.title = title;
        this.headCount = headCount;
        this.currentCount = 1;
        this.operationWay = operationWay;
        this.expectedDate = expectedDate;
        this.estimatedDuration = estimatedDuration;
        this.content = content;
        this.status = PROGRESS;
        this.host = host;
        this.participations.add(new Participation(host, this));
        this.items.addAll(
                items.stream()
                        .map(name -> new Item(this, name))
                        .collect(Collectors.toList())
        );
    }

    public void addPostHashtag(List<PostHashtag> postHashtags) {
        this.postHashtags.addAll(postHashtags);
    }

    public boolean isHost(Member member) {
        return host.equals(member);
    }

    public void update(String title, Integer headCount, String operationWay, String expectedDate,
                       String estimatedDuration, String content, List<PostHashtag> postHashtags, List<String> items) {
        this.title = title;
        this.headCount = headCount;
        this.operationWay = operationWay;
        this.expectedDate = expectedDate;
        this.estimatedDuration = estimatedDuration;
        this.content = content;
        this.postHashtags.removeIf(postHashtag -> !postHashtags.contains(postHashtag));
        this.postHashtags.addAll(
                postHashtags.stream()
                        .filter(p -> !this.postHashtags.contains(p))
                        .collect(Collectors.toList())
        );
        List<Item> i = items.stream()
                .map(name -> new Item(this, name))
                .collect(Collectors.toList());
        this.items.removeIf(item -> !i.contains(item));
        this.items.addAll(
                i.stream()
                        .filter(item -> !this.items.contains(item))
                        .collect(Collectors.toList())
        );
    }

    public Participation attend(Member member) {
        if (isHost(member)) {
            throw new IllegalStateException("작성자는 신청할 수 없습니다.");
        }
        if (currentCount >= headCount) {
            throw new IllegalStateException("모집 정원이 초과되었습니다.");
        }
        Participation participation = new Participation(member, this);
        participations.add(participation);
        currentCount++;
        return participation;
    }

    public void cancel(Member member) {
        if (isHost(member)) {
            throw new IllegalStateException("작성자는 취소할 수 없습니다.");
        }
        Participation participation = participations.stream()
                .filter(p -> p.isMatch(member))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("신청한 회원만 취소할 수 있습니다."));
        participations.remove(participation);
        currentCount--;
    }

    public void complete(Member member) {
        if (!isHost(member)) {
            throw new UnAuthorizedException("작성자만 모집을 완료할 수 있습니다.");
        }
        this.status = COMPLETED;
    }

    public void end(Member member) {
        if (!isHost(member)) {
            throw new UnAuthorizedException("작성자만 종료할 수 있습니다.");
        }
        generateEvaluations();
        this.status = END;
    }

    public void addComment(Member member, String comment) {
        this.comments.add(new Comment(this, member, comment));
    }

    public void removeComment(Member member, Long commentId) {
        Comment comment = comments.stream()
                .filter(it -> it.getId().equals(commentId))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("댓글(commentId: " + commentId + ")이 존재하지 않습니다."));
        if (!comment.isAuthor(member)) {
            throw new UnAuthorizedException("작성자만 삭제할 수 있습니다.");
        }
        comments.remove(comment);
    }

    private void generateEvaluations() {
        for (int i = 0; i < participations.size(); i++) {
            for (int j = 0; j < participations.size(); j++) {
                if (i == j) continue;
                Member evaluator = participations.get(i).getMember();
                Member evaluatee = participations.get(j).getMember();
                Evaluation evaluation = new Evaluation(evaluator, evaluatee, this);
                evaluations.add(evaluation);
            }
        }
    }
}
