package com.moyeou.moyeoubackend.notification.domain;

import lombok.Getter;

@Getter
public enum NotificationType {
    ATTEND, CANCEL, ACCEPT, REJECT, COMPLETE, END, COMMENT
}
