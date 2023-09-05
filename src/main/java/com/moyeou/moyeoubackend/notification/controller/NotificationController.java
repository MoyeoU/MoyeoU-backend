package com.moyeou.moyeoubackend.notification.controller;

import com.moyeou.moyeoubackend.auth.supports.LoginMember;
import com.moyeou.moyeoubackend.notification.controller.response.NotificationResponse;
import com.moyeou.moyeoubackend.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@LoginMember Long memberId) {
        return notificationService.connect(memberId);
    }

    @GetMapping(value = "/notifications")
    public List<NotificationResponse> notifications(@LoginMember Long memberId) {
        return notificationService.checkNotification(memberId);
    }
}
