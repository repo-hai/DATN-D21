package com.DATN.Bej.dto.request.fcm;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendPushNotificationRequest {
    
    /**
     * FCM token của thiết bị (lấy từ /api/device-token/register)
     */
    String deviceToken;
    
    /**
     * Tiêu đề notification
     */
    String title;
    
    /**
     * Nội dung notification
     */
    String body;
    
    /**
     * [Optional] Custom data key
     */
    String dataKey;
    
    /**
     * [Optional] Custom data value
     */
    String dataValue;
}
