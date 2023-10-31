package com.moyeou.moyeoubackend.notification.controller;

import com.moyeou.moyeoubackend.auth.supports.LoginMember;
import com.moyeou.moyeoubackend.notification.controller.response.NotificationResponse;
import com.moyeou.moyeoubackend.notification.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Notification", description = "알림 관련 API")
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "연결")
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@LoginMember Long memberId) {
        return notificationService.connect(memberId);
    }

    @Operation(summary = "알림 조회")
    @GetMapping(value = "/notifications")
    public List<NotificationResponse> notifications(@LoginMember Long memberId) {
        return notificationService.checkNotification(memberId);
    }

    @Operation(summary = "알림 삭제")
    @PostMapping(value = "/notifications/{notificationId}")
    public void delete(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
    }
}
