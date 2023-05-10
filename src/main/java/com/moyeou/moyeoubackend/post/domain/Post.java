package com.moyeou.moyeoubackend.post.domain;

import com.moyeou.moyeoubackend.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

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
    @Id @GeneratedValue(strategy = IDENTITY)
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

    @Builder
    public Post(String title, Integer headCount, Integer currentCount, String operationWay, String expectedDate,
                String estimatedDuration, String content, PostStatus status, Member host, List<PostHashtag> postHashtags) {
        this.title = title;
        this.headCount = headCount;
        this.currentCount = currentCount;
        this.operationWay = operationWay;
        this.expectedDate = expectedDate;
        this.estimatedDuration = estimatedDuration;
        this.content = content;
        this.status = status;
        this.host = host;
        this.postHashtags = postHashtags;
    }

    public void addPostHashtag(List<PostHashtag> postHashtags) {
        this.postHashtags = postHashtags;
    }

    public boolean isHost(Member member) {
        return host.equals(member);
    }
}
