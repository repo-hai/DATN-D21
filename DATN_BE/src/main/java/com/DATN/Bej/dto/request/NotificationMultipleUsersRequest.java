package com.DATN.Bej.dto.request;

import com.DATN.Bej.dto.ApiNotificationRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationMultipleUsersRequest {
    List<String> userIds;
    ApiNotificationRequest notification;
}

