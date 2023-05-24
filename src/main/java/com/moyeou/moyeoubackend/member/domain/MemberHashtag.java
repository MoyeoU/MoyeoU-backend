package com.moyeou.moyeoubackend.member.domain;

import com.moyeou.moyeoubackend.post.domain.Hashtag;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "member_hashtag")
@NoArgsConstructor(access = PROTECTED)
public class MemberHashtag {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_hashtag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    public MemberHashtag(Member member, Hashtag hashtag) {
        this.member = member;
        this.hashtag = hashtag;
    }
}
