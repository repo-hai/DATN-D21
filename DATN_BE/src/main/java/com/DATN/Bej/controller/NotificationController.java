package com.DATN.Bej.controller;

import com.DATN.Bej.dto.ApiNotificationRequest;
import com.DATN.Bej.dto.request.ApiResponse;
import com.DATN.Bej.dto.request.NotificationMultipleUsersRequest;
import com.DATN.Bej.dto.response.NotificationResponse;
import com.DATN.Bej.entity.identity.User;
import com.DATN.Bej.event.BroadcastNotificationEvent;
import com.DATN.Bej.event.NotificationSendEvent;
import com.DATN.Bej.exception.AppException;
import com.DATN.Bej.exception.ErrorCode;
import com.DATN.Bej.repository.UserRepository;
import com.DATN.Bej.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor 
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) 
public class NotificationController {

    NotificationService notificationService;
    ApplicationEventPublisher eventPublisher;
    UserRepository userRepository;
    
    /**
     * Helper method: Lấy userId từ phoneNumber (principal.getName())
     */
    private String getUserIdFromPrincipal(Principal principal) {
        if (principal == null) {
            return null;
        }
        String phoneNumber = principal.getName();
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return user.getId();
    }

    /**
     * API gửi cá nhân (dùng bởi admin/service khác)
     * Yêu cầu: ROLE_ADMIN
     * Sử dụng event để tự động gửi qua WebSocket, Firebase và lưu vào database
     */
    @PostMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> sendToUserById(
            @PathVariable String userId,
            @RequestBody ApiNotificationRequest request) {
        
        log.info("📨 Admin sending notification to user: {}", userId);
        
        // Publish event - EventListener sẽ tự động xử lý
        eventPublisher.publishEvent(new NotificationSendEvent(userId, request));
        
        return ApiResponse.<Void>builder()
                .message("Notification event published for user: " + userId)
                .build();
    }

    /**
     * API gửi notification cho nhiều người
     * Yêu cầu: ROLE_ADMIN
     */
    @PostMapping("/multiple-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> sendToMultipleUsers(@RequestBody NotificationMultipleUsersRequest request) {
        log.info("📨 Admin sending notification to {} users", request.getUserIds().size());
        
        notificationService.sendNotificationsToMultipleUsers(
                request.getUserIds(), 
                request.getNotification()
        );
        
        return ApiResponse.<Void>builder()
                .message("Notifications sent to " + request.getUserIds().size() + " users")
                .build();
    }

    /**
     * API gửi broadcast (dùng bởi admin/service khác)
     * Yêu cầu: ROLE_ADMIN
     * Sử dụng event để tự động gửi qua WebSocket, Firebase và lưu vào database cho tất cả users
     */
    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> sendBroadcastNotification(@RequestBody ApiNotificationRequest request) {
        log.info("📢 Admin sending broadcast notification - Title: {}", request.title());
        
        // Publish event - EventListener sẽ tự động xử lý
        eventPublisher.publishEvent(new BroadcastNotificationEvent(request));
        
        return ApiResponse.<Void>builder()
                .message("Broadcast notification event published")
                .build();
    }

    /**
     * API lấy TẤT CẢ notifications của user hiện tại
     * Trả về danh sách NotificationResponse
     */
    @GetMapping("/my-notifications")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getAllMyNotifications(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.<List<NotificationResponse>>builder()
                        .code(1001)
                        .message("User not authenticated")
                        .build()
            );
        }
        
        String userId = getUserIdFromPrincipal(principal);
        List<NotificationResponse> notifications = notificationService.getAllNotificationsForUser(userId);
        
        return ResponseEntity.ok(
            ApiResponse.<List<NotificationResponse>>builder()
                    .result(notifications)
                    .build()
        );
    }

    /**
     * API lấy số lượng notification chưa đọc
     */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.<Map<String, Long>>builder()
                        .code(1001)
                        .message("User not authenticated")
                        .build()
            );
        }
        
        String userId = getUserIdFromPrincipal(principal);
        long count = notificationService.countUnreadNotifications(userId);
        
        Map<String, Long> result = new HashMap<>();
        result.put("unreadCount", count);
        
        return ResponseEntity.ok(
            ApiResponse.<Map<String, Long>>builder()
                    .result(result)
                    .build()
        );
    }

    /**
     * API lấy danh sách notification chưa đọc
     */
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.<List<NotificationResponse>>builder()
                        .code(1001)
                        .message("User not authenticated")
                        .build()
            );
        }
        
        String userId = getUserIdFromPrincipal(principal);
        List<NotificationResponse> notifications = notificationService.getUnreadNotificationsForUser(userId);
        
        return ResponseEntity.ok(
            ApiResponse.<List<NotificationResponse>>builder()
                    .result(notifications)
                    .build()
        );
    }

    /**
     * API đánh dấu một notification là ĐÃ ĐỌC
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable String notificationId,
            Principal principal) {
                
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.<Void>builder()
                        .code(1001) 
                        .message("User not authenticated")
                        .build()
            );
        }
        
        try {
            String userId = getUserIdFromPrincipal(principal);
            boolean success = notificationService.markAsRead(notificationId, userId);
            
            if (success) {
                return ResponseEntity.ok(
                    ApiResponse.<Void>builder()
                            .message("Notification marked as read")
                            .build()
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<Void>builder()
                            .code(1004)
                            .message("Notification not found")
                            .build()
                );
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiResponse.<Void>builder()
                        .code(1003)
                        .message(e.getMessage())
                        .build()
            );
        }
    }

    /**
     * API đánh dấu TẤT CẢ notifications là đã đọc (toggle)
     * Nếu tất cả đã đọc -> đánh dấu tất cả là chưa đọc
     * Nếu có ít nhất 1 chưa đọc -> đánh dấu tất cả là đã đọc
     */
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Map<String, Object>>> markAllAsRead(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.<Map<String, Object>>builder()
                        .code(1001)
                        .message("User not authenticated")
                        .build()
            );
        }
        
        String userId = getUserIdFromPrincipal(principal);
        int updatedCount = notificationService.toggleMarkAllAsRead(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("updatedCount", updatedCount);
        result.put("message", "All notifications toggled");
        
        return ResponseEntity.ok(
            ApiResponse.<Map<String, Object>>builder()
                    .result(result)
                    .message("Successfully toggled " + updatedCount + " notifications")
                    .build()
        );
    }
}