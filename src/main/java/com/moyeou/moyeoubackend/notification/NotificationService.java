package com.moyeou.moyeoubackend.notification;

import com.moyeou.moyeoubackend.notification.controller.response.NotificationResponse;
import com.moyeou.moyeoubackend.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private static final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final NotificationRepository notificationRepository;

    public SseEmitter connect(Long userId) {
        SseEmitter emitter = new SseEmitter(60L * 1000 * 60);
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        return emitter;
    }

    public void sendMessage(Long id, String data) {
        try {
            SseEmitter emitter = emitters.get(id);
            SseEmitter.SseEventBuilder sse = SseEmitter.event()
                    .id(String.valueOf(id))
                    .name("sse").data(data);
            emitter.send(sse);
        } catch (Exception exception) {
            log.warn("connect x");
        }
    }

    public List<NotificationResponse> checkNotification(Long memberId) {
        return notificationRepository.findAllByReceiverId(memberId)
                .stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }
}
