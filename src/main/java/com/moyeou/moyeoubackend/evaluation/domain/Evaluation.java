package com.moyeou.moyeoubackend.evaluation.domain;

import com.moyeou.moyeoubackend.member.domain.Member;
import com.moyeou.moyeoubackend.post.domain.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Evaluation {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "evaluation_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "evaluator_id")
    private Member evaluator;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "evaluatee_id")
    private Member evaluatee;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "point")
    private Double point;

    @Column(name = "content")
    private String content;

    @Column(name = "evaluated")
    private Boolean evaluated;

    public Evaluation(Member evaluator, Member evaluatee, Post post) {
        this.evaluator = evaluator;
        this.evaluatee = evaluatee;
        this.post = post;
        this.evaluated = false;
    }

    public void evaluate(Double point, String content) {
        this.point = point;
        this.content = content;
        this.evaluated = true;
        this.getEvaluatee().calculatePoint(point);
    }

    public boolean isEvaluator(Member member) {
        return this.evaluator.equals(member);
    }
}
