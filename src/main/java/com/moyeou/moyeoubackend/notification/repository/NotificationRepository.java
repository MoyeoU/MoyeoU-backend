package com.moyeou.moyeoubackend.notification.repository;

import com.moyeou.moyeoubackend.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByReceiverId(Long receiverId);
}
