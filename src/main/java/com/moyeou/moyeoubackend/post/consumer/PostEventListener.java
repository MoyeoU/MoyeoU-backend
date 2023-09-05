package com.moyeou.moyeoubackend.post.consumer;

import com.moyeou.moyeoubackend.member.domain.Member;
import com.moyeou.moyeoubackend.member.repository.MemberRepository;
import com.moyeou.moyeoubackend.notification.NotificationService;
import com.moyeou.moyeoubackend.notification.domain.Notification;
import com.moyeou.moyeoubackend.notification.domain.NotificationType;
import com.moyeou.moyeoubackend.notification.repository.NotificationRepository;
import com.moyeou.moyeoubackend.post.domain.Participation;
import com.moyeou.moyeoubackend.post.domain.Post;
import com.moyeou.moyeoubackend.post.domain.event.*;
import com.moyeou.moyeoubackend.post.repository.ParticipationRepository;
import com.moyeou.moyeoubackend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostEventListener {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final ParticipationRepository participationRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void handleAttendEvent(AttendEvent attendEvent) {
        Member member = memberRepository.findById(attendEvent.getMemberId()).get();
        Post post = postRepository.findById(attendEvent.getPostId()).orElseThrow();
        Long hostId = post.getHost().getId();

        notificationService.sendMessage(hostId, "attend");
        Notification notification = new Notification(hostId, NotificationType.ATTEND, member, post);
        notificationRepository.save(notification);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void handleCancelEvent(CancelEvent cancelEvent) {
        Member member = memberRepository.findById(cancelEvent.getMemberId()).orElseThrow();
        Post post = postRepository.findById(cancelEvent.getPostId()).orElseThrow();
        Long hostId = post.getHost().getId();

        notificationService.sendMessage(hostId, "cancel");
        Notification notification = new Notification(hostId, NotificationType.CANCEL, member, post);
        notificationRepository.save(notification);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void handleAcceptEvent(AcceptEvent acceptEvent) {
        Post post = postRepository.findById(acceptEvent.getPostId()).orElseThrow();
        Participation participation = participationRepository.findById(acceptEvent.getParticipationId()).orElseThrow();
        Long attendId = participation.getMember().getId();

        notificationService.sendMessage(attendId, "accept");
        Notification notification = new Notification(attendId, NotificationType.ACCEPT, post);
        notificationRepository.save(notification);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void handleRejectEvent(RejectEvent rejectEvent) {
        Post post = postRepository.findById(rejectEvent.getPostId()).orElseThrow();
        Participation participation = participationRepository.findById(rejectEvent.getParticipationId()).orElseThrow();
        Long attendId = participation.getMember().getId();

        notificationService.sendMessage(attendId, "reject");

        Notification notification = new Notification(attendId, NotificationType.REJECT, post);
        notificationRepository.save(notification);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void handleCompleteEvent(CompleteEvent completeEvent) {
        Post post = postRepository.findById(completeEvent.getPostId()).orElseThrow();

        for (Participation participation : post.getParticipations()) {
            Long attendId = participation.getMember().getId();
            notificationService.sendMessage(attendId, "complete");
            Notification notification = new Notification(attendId, NotificationType.COMPLETE, post);
            notificationRepository.save(notification);
        }
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void handleEndEvent(EndEvent endEvent) {
        Post post = postRepository.findById(endEvent.getPostId()).orElseThrow();

        for (Participation participation : post.getParticipations()) {
            Long attendId = participation.getMember().getId();
            notificationService.sendMessage(attendId, "end");
            Notification notification = new Notification(attendId, NotificationType.END, post);
            notificationRepository.save(notification);
        }
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void handleCommentEvent(CommentEvent commentEvent) {
        Post post = postRepository.findById(commentEvent.getPostId()).orElseThrow();
        Long hostId = post.getHost().getId();

        notificationService.sendMessage(hostId, "comment");
        Notification notification = new Notification(hostId, NotificationType.COMMENT, post);
        notificationRepository.save(notification);
    }
}
