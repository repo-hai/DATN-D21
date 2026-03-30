package com.DATN.Bej.event;

import com.DATN.Bej.dto.ApiNotificationRequest;

/**
 * Event được phát ra khi admin muốn gửi thông báo broadcast cho tất cả users
 */
public record BroadcastNotificationEvent(
    ApiNotificationRequest request  // Thông tin thông báo
) {}

