package com.moyeou.moyeoubackend.post.domain;

import com.moyeou.moyeoubackend.member.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "comment")
@NoArgsConstructor(access = PROTECTED)
public class Comment {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Comment(Post post, Member member, String content) {
        this.post = post;
        this.member = member;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public boolean isAuthor(Member member) {
        return this.member.equals(member);
    }

    public void update(String content) {
        this.content = content;
    }
}
