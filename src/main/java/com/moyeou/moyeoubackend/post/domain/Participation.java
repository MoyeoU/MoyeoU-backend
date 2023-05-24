package com.moyeou.moyeoubackend.post.domain;

import com.moyeou.moyeoubackend.member.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "participation")
@NoArgsConstructor(access = PROTECTED)
public class Participation {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "participation_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "participated_at", nullable = false)
    private LocalDateTime participatedAt;

    @OneToMany(mappedBy = "participation", cascade = CascadeType.ALL)
    private List<Answer> answers = new ArrayList<>();

    public Participation(Member member, Post post) {
        this.member = member;
        this.post = post;
        this.participatedAt = LocalDateTime.now();
    }

    public void addAnswers(List<Answer> answers) {
        this.answers.addAll(answers);
    }

    public boolean isMatch(Member member) {
        return this.member.equals(member);
    }
}
