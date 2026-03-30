package com.DATN.Bej.service;

import com.DATN.Bej.dto.ApiNotificationRequest;
import com.DATN.Bej.dto.NotificationPayload;
import com.DATN.Bej.dto.response.NotificationResponse;
import com.DATN.Bej.entity.Notification;
import com.DATN.Bej.entity.identity.User;
import com.DATN.Bej.repository.NotificationRepository;
import com.DATN.Bej.repository.UserRepository;
import com.DATN.Bej.service.FcmDeviceTokenService;
import com.DATN.Bej.service.FirebaseMessagingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {

    SimpMessagingTemplate messagingTemplate;
    NotificationRepository notificationRepository;
    UserRepository userRepository;
    FcmDeviceTokenService fcmDeviceTokenService;
    FirebaseMessagingService firebaseMessagingService;

    private static final String USER_QUEUE = "/queue/notifications";


    /**
     * Tạo và gửi thông báo cá nhân cho một user
     * Thực hiện 3 công việc:
     * 1. Lưu thông báo vào database
     * 2. Gửi qua WebSocket (RabbitMQ STOMP broker)
     * 3. Gửi qua Firebase (nếu user có FCM token)
     * 
     * Method này được sử dụng bởi:
     * - Event Listener: NotificationSendEvent, OrderStatusUpdateEvent
     * - API: NotificationController.sendToUserById()
     * - Internal: sendNotificationsToMultipleUsers()
     * 
     * @param userId ID của user nhận thông báo
     * @param request Thông tin thông báo
     */
    @Transactional
    public void createAndSendPersonalNotification(String userId, ApiNotificationRequest request) {
        log.info("📨 Creating and sending personal notification to user: {}", userId);

        // 1. Lưu thông báo vào database
        User recipient = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Notification notification = Notification.builder()
                .recipient(recipient)
                .type(request.type())
                .title(request.title())
                .body(request.body())
                .isRead(false)
                .resourceId(request.metadata() != null ? request.metadata().values().stream().findFirst().orElse(null) : null)
                .build();
        notificationRepository.save(notification);
        log.info("✅ Notification saved to database - ID: {}", notification.getId());

        // 2. Gửi qua WebSocket
        try {
            NotificationPayload payload = new NotificationPayload(
                UUID.fromString(notification.getId()),
                request.type(),
                request.title(),
                request.body(),
                Instant.now(),
                request.metadata()
            );
            
            // Gửi qua queue-based destination (RabbitMQ STOMP broker yêu cầu queue name cụ thể)
            // RabbitMQ STOMP broker không tự động tạo topic exchange cho dynamic destinations
            // Sử dụng queue với tên cụ thể: /queue/notifications.{userId}
            // Client cần subscribe to /queue/notifications.{userId}
            String queueDestination = "/queue/notifications." + userId;
            messagingTemplate.convertAndSend(queueDestination, payload);
            
            log.info("✅ Notification sent via WebSocket to user: {} (queue: {})", userId, queueDestination);
        } catch (Exception e) {
            log.error("❌ Failed to send notification via WebSocket to user {}: {}", userId, e.getMessage(), e);
        }

        // 3. Gửi qua Firebase (nếu user có FCM token)
        try {
            List<String> deviceTokens = fcmDeviceTokenService.getActiveTokensForUser(userId);
            if (!deviceTokens.isEmpty()) {
                for (String token : deviceTokens) {
                    try {
                        firebaseMessagingService.sendNotificationToDevice(
                            token,
                            request.title(),
                            request.body()
                        );
                        log.info("✅ FCM notification sent to device: {}", token);
                    } catch (Exception e) {
                        log.error("❌ Failed to send FCM notification to device {}: {}", token, e.getMessage());
                        // Xóa token không hợp lệ
                        fcmDeviceTokenService.deleteTokenByValue(token);
                    }
                }
            } else {
                log.info("ℹ️ No FCM tokens found for user: {}", userId);
            }
        } catch (Exception e) {
            log.error("❌ Failed to send FCM notifications to user {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Gửi thông báo broadcast cho tất cả users
     * Thực hiện 3 công việc:
     * 1. Lưu thông báo vào database cho tất cả users
     * 2. Gửi qua WebSocket (broadcast - RabbitMQ STOMP broker)
     * 3. Gửi qua Firebase cho tất cả users có FCM token
     * 
     * Method này được sử dụng bởi:
     * - Event Listener: BroadcastNotificationEvent
     * - API: NotificationController.sendBroadcastNotification()
     * 
     * @param request Thông tin thông báo
     */
    @Transactional
    public void sendBroadcast(ApiNotificationRequest request) {
        log.info("📢 Sending broadcast notification - Title: {}", request.title());

        // 1. Lưu thông báo vào database cho tất cả users
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            Notification notification = Notification.builder()
                    .recipient(user)
                    .type(request.type())
                    .title(request.title())
                    .body(request.body())
                    .isRead(false)
                    .resourceId(request.metadata() != null ? request.metadata().values().stream().findFirst().orElse(null) : null)
                    .build();
            notificationRepository.save(notification);
        }
        log.info("✅ Broadcast notifications saved to database for {} users", allUsers.size());

        // 2. Gửi qua WebSocket (broadcast)
        try {
            NotificationPayload payload = new NotificationPayload(
                UUID.randomUUID(),
                request.type(),
                request.title(),
                request.body(),
                Instant.now(),
                request.metadata()
            );
            messagingTemplate.convertAndSend("/topic/notifications", payload);
            log.info("✅ Broadcast notification sent via WebSocket");
        } catch (Exception e) {
            log.error("❌ Failed to send broadcast notification via WebSocket: {}", e.getMessage());
        }

        // 3. Gửi qua Firebase cho tất cả users có FCM token
        try {
            int successCount = 0;
            int failCount = 0;
            
            for (User user : allUsers) {
                List<String> deviceTokens = fcmDeviceTokenService.getActiveTokensForUser(user.getId());
                for (String token : deviceTokens) {
                    try {
                        firebaseMessagingService.sendNotificationToDevice(
                            token,
                            request.title(),
                            request.body()
                        );
                        successCount++;
                    } catch (Exception e) {
                        log.error("❌ Failed to send FCM notification to device {}: {}", token, e.getMessage());
                        failCount++;
                        // Xóa token không hợp lệ
                        fcmDeviceTokenService.deleteTokenByValue(token);
                    }
                }
            }
            log.info("✅ Broadcast FCM notifications sent - Success: {}, Failed: {}", successCount, failCount);
        } catch (Exception e) {
            log.error("❌ Failed to send broadcast FCM notifications: {}", e.getMessage());
        }
    }
    

    /**
     * Lấy tất cả notifications của user
     * @param userId ID của user
     * @return Danh sách NotificationResponse
     */
    public List<NotificationResponse> getAllNotificationsForUser(String userId) {
        List<Notification> notifications = notificationRepository.findByRecipient_IdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::toNotificationResponse)
                .collect(Collectors.toList());
    }

    /**
     * Đếm số notification chưa đọc của user
     * @param userId ID của user
     * @return Số lượng notification chưa đọc
     */
    public long countUnreadNotifications(String userId) {
        return notificationRepository.countByRecipient_IdAndIsReadFalse(userId);
    }

    /**
     * Lấy danh sách notification chưa đọc của user
     * @param userId ID của user
     * @return Danh sách NotificationResponse chưa đọc
     */
    public List<NotificationResponse> getUnreadNotificationsForUser(String userId) {
        List<Notification> notifications = notificationRepository.findByRecipient_IdAndIsReadFalseOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::toNotificationResponse)
                .collect(Collectors.toList());
    }

    /**
     * Đánh dấu một notification là đã đọc
     * @param notificationId ID của notification
     * @param userId ID của user
     * @return true nếu thành công, false nếu không tìm thấy
     */
    @Transactional
    public boolean markAsRead(String notificationId, String userId) {
        Optional<Notification> notifOpt = notificationRepository.findById(notificationId);
        
        if (notifOpt.isEmpty()) return false;
        
        Notification notification = notifOpt.get();
        
        if (!notification.getRecipient().getId().equals(userId)) {
            throw new SecurityException("User does not have permission to read this notification");
        }
        
        notification.setRead(true);
        notificationRepository.save(notification);
        return true;
    }

    /**
     * Đánh dấu tất cả notifications của user là đã đọc (toggle)
     * Nếu tất cả đã đọc -> đánh dấu tất cả là chưa đọc
     * Nếu có ít nhất 1 chưa đọc -> đánh dấu tất cả là đã đọc
     * @param userId ID của user
     * @return Số lượng notifications đã được cập nhật
     */
    @Transactional
    public int toggleMarkAllAsRead(String userId) {
        List<Notification> allNotifications = notificationRepository.findByRecipient_IdOrderByCreatedAtDesc(userId);
        
        if (allNotifications.isEmpty()) {
            return 0;
        }

        // Kiểm tra xem có notification nào chưa đọc không
        boolean hasUnread = allNotifications.stream().anyMatch(n -> !n.isRead());
        
        // Nếu có notification chưa đọc -> đánh dấu tất cả là đã đọc
        // Nếu tất cả đã đọc -> đánh dấu tất cả là chưa đọc
        boolean targetReadStatus = hasUnread;
        
        for (Notification notification : allNotifications) {
            notification.setRead(targetReadStatus);
        }
        
        notificationRepository.saveAll(allNotifications);
        log.info("✅ Toggled all notifications for user {} - New status: {}, Count: {}", 
                userId, targetReadStatus ? "READ" : "UNREAD", allNotifications.size());
        
        return allNotifications.size();
    }

    /**
     * Gửi notification cho nhiều người
     * Method này gọi createAndSendPersonalNotification() cho mỗi user,
     * đảm bảo mỗi notification được:
     * 1. Lưu vào database
     * 2. Gửi qua WebSocket (RabbitMQ STOMP broker)
     * 3. Gửi qua Firebase (nếu user có FCM token)
     * 
     * @param userIds Danh sách ID của các users
     * @param request Thông tin notification
     */
    @Transactional
    public void sendNotificationsToMultipleUsers(List<String> userIds, ApiNotificationRequest request) {
        log.info("📨 Sending notification to {} users - Title: {}", userIds.size(), request.title());
        
        int successCount = 0;
        int failCount = 0;
        
        for (String userId : userIds) {
            try {
                createAndSendPersonalNotification(userId, request);
                successCount++;
            } catch (Exception e) {
                log.error("❌ Failed to send notification to user {}: {}", userId, e.getMessage());
                failCount++;
            }
        }
        
        log.info("✅ Sent notifications to multiple users - Success: {}, Failed: {}", successCount, failCount);
    }

    /**
     * Convert Notification entity to NotificationResponse DTO
     */
    private NotificationResponse toNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .body(notification.getBody())
                .isRead(notification.isRead())
                .resourceId(notification.getResourceId())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    /**
     * Deprecated: Sử dụng getAllNotificationsForUser thay thế
     */
    @Deprecated
    public List<Notification> getHistoryForUser(String userId) {
        return notificationRepository.findByRecipient_IdOrderByCreatedAtDesc(userId);
    }
}