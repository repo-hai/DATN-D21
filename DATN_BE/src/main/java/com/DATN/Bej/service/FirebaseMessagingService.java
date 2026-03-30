package com.DATN.Bej.service;

import com.DATN.Bej.exception.AppException;
import com.DATN.Bej.exception.ErrorCode;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FirebaseMessagingService {

    /**
     * Gửi notification tới một thiết bị cụ thể bằng FCM token
     * @param deviceToken FCM token từ client
     * @param title Tiêu đề notification
     * @param body Nội dung notification
     * @return Message ID từ Firebase
     */
    public String sendNotificationToDevice(String deviceToken, String title, String body) {
        try {
            log.info("📤 Sending FCM notification to device: {} | Title: {}", deviceToken, title);
            
            Message message = Message.builder()
                .setToken(deviceToken)
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build())
                .build();

            String messageId = FirebaseMessaging.getInstance().send(message);
            log.info("✅ FCM notification sent successfully. Message ID: {}", messageId);
            
            return messageId;
        } catch (Exception e) {
            log.error("❌ Failed to send FCM notification to device {}: {}", deviceToken, e.getMessage());
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    /**
     * Gửi notification với data payload (dùng cho custom handling)
     * @param deviceToken FCM token
     * @param title Tiêu đề
     * @param body Nội dung
     * @param dataKey Custom data key
     * @param dataValue Custom data value
     * @return Message ID
     */
    public String sendNotificationWithData(String deviceToken, String title, String body, 
                                          String dataKey, String dataValue) {
        try {
            log.info("📤 Sending FCM notification with data to device: {}", deviceToken);
            
            Message message = Message.builder()
                .setToken(deviceToken)
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build())
                .putData(dataKey, dataValue)
                .build();

            String messageId = FirebaseMessaging.getInstance().send(message);
            log.info("✅ FCM notification with data sent successfully. Message ID: {}", messageId);
            
            return messageId;
        } catch (Exception e) {
            log.error("❌ Failed to send FCM notification with data: {}", e.getMessage());
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    /**
     * Gửi notification tới nhiều devices (topic broadcast)
     * @param topic Topic name
     * @param title Tiêu đề
     * @param body Nội dung
     * @return Message ID
     */
    public String sendNotificationToTopic(String topic, String title, String body) {
        try {
            log.info("📢 Broadcasting FCM notification to topic: {}", topic);
            
            Message message = Message.builder()
                .setTopic(topic)
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build())
                .build();

            String messageId = FirebaseMessaging.getInstance().send(message);
            log.info("✅ FCM broadcast sent successfully. Message ID: {}", messageId);
            
            return messageId;
        } catch (Exception e) {
            log.error("❌ Failed to send FCM broadcast: {}", e.getMessage());
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }
}
