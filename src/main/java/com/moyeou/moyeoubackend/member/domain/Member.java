package com.moyeou.moyeoubackend.member.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = PROTECTED)
public class Member {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "department", nullable = false)
    private String department;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "introduction")
    private String introduction;

    @Column(name = "point")
    private Double point;

    @OneToMany(mappedBy = "member", cascade = ALL, orphanRemoval = true)
    private List<MemberHashtag> memberHashtags = new ArrayList<>();

    @Builder
    public Member(String email, String department, String nickname, String password) {
        this.email = email;
        this.department = department;
        this.nickname = nickname;
        this.password = password;
    }

    public void update(String introduction, String nickname, String imagePath, List<MemberHashtag> memberHashtags) {
        this.introduction = introduction;
        this.nickname = nickname;
        this.imagePath = imagePath;
        this.memberHashtags.removeIf(hashtag -> !memberHashtags.contains(hashtag));
        this.memberHashtags.addAll(
                memberHashtags.stream()
                        .filter(hashtag -> !this.memberHashtags.contains(hashtag))
                        .collect(Collectors.toList())
        );
    }
}
