package com.DATN.Bej.dto.response;

import com.DATN.Bej.enums.NotificationType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    String id;
    NotificationType type;
    String title;
    String body;
    boolean isRead;
    String resourceId;
    Instant createdAt;
}

