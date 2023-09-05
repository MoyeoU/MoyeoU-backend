package com.moyeou.moyeoubackend.notification.domain;

import com.moyeou.moyeoubackend.member.domain.Member;
import com.moyeou.moyeoubackend.post.domain.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "notification")
@NoArgsConstructor(access = PROTECTED)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Enumerated(value = STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public Notification(Long receiverId, NotificationType type, Member member, Post post) {
        this.receiverId = receiverId;
        this.type = type;
        this.member = member;
        this.post = post;
    }

    public Notification(Long receiverId, NotificationType type, Post post) {
        this.receiverId = receiverId;
        this.type = type;
        this.post = post;
    }
}
