package com.DATN.Bej.dto.response.fcm;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendPushNotificationResponse {
    
    /**
     * Message ID từ Firebase (xác nhận đã gửi)
     */
    String messageId;
    
    /**
     * FCM token của thiết bị nhận
     */
    String deviceToken;
    
    /**
     * Status: "success" hoặc "failed"
     */
    String status;
}
