package com.moyeou.moyeoubackend.notification.controller.response;

import com.moyeou.moyeoubackend.member.domain.Member;
import com.moyeou.moyeoubackend.notification.domain.Notification;
import com.moyeou.moyeoubackend.notification.domain.NotificationType;
import com.moyeou.moyeoubackend.post.domain.Participation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long notificationId;
    private Boolean deleted;
    private Long receiverId;
    private NotificationType type;
    private Long memberId;
    private String memberNickname;
    private Long postId;
    private String postTitle;
    private Long participationId;

    public static NotificationResponse from(Notification notification) {
        Long memberId = Optional.ofNullable(notification.getMember())
                .map(Member::getId).orElse(null);
        String memberNickname = Optional.ofNullable(notification.getMember())
                .map(Member::getNickname).orElse(null);
        Long participationId = notification.getPost().getParticipations()
                .stream()
                .filter(participation -> participation.isMatch(notification.getMember()))
                .findAny()
                .map(Participation::getId).orElse(null);

        return new NotificationResponse(
                notification.getId(),
                notification.getDeleted(),
                notification.getReceiverId(),
                notification.getType(),
                memberId,
                memberNickname,
                notification.getPost().getId(),
                notification.getPost().getTitle(),
                participationId
        );
    }
}
