package com.DATN.Bej.controller;

import com.DATN.Bej.dto.request.ApiResponse;
import com.DATN.Bej.dto.request.fcm.RegisterFcmTokenRequest;
import com.DATN.Bej.dto.request.fcm.SendPushNotificationRequest;
import com.DATN.Bej.dto.response.fcm.FcmTokenResponse;
import com.DATN.Bej.dto.response.fcm.SendPushNotificationResponse;
import com.DATN.Bej.service.FirebaseMessagingService;
import com.DATN.Bej.service.FcmDeviceTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/device-token")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FcmDeviceTokenController {

    FcmDeviceTokenService fcmDeviceTokenService;
    FirebaseMessagingService firebaseMessagingService;

    /**
     * API đăng ký FCM token
     * POST /api/device-token/register
     */
    @PostMapping("/register")
    ApiResponse<FcmTokenResponse> registerToken(@RequestBody RegisterFcmTokenRequest request) {
        log.info("📱 FCM token registration request received");
        
        String userId = getCurrentUserId();
        FcmTokenResponse response = fcmDeviceTokenService.registerToken(userId, request);
        
        return ApiResponse.<FcmTokenResponse>builder()
            .result(response)
            .message("FCM token registered successfully")
            .build();
    }

    /**
     * API xóa một token cụ thể
     * DELETE /api/device-token/{tokenId}
     */
    @DeleteMapping("/{tokenId}")
    ApiResponse<Void> deleteToken(@PathVariable String tokenId) {
        log.info("🗑️ Delete FCM token request: {}", tokenId);
        
        String userId = getCurrentUserId();
        fcmDeviceTokenService.deleteToken(tokenId, userId);
        
        return ApiResponse.<Void>builder()
            .message("FCM token deleted successfully")
            .build();
    }

    /**
     * API xóa tất cả tokens của user (logout)
     * DELETE /api/device-token/all
     */
    @DeleteMapping("/all")
    ApiResponse<Void> deleteAllTokens() {
        log.info("🗑️ Delete all FCM tokens request");
        
        String userId = getCurrentUserId();
        fcmDeviceTokenService.deleteAllTokensForUser(userId);
        
        return ApiResponse.<Void>builder()
            .message("All FCM tokens deleted successfully")
            .build();
    }

    /**
     * API đẩy notification tới một thiết bị cụ thể (TEST - PUBLIC)
     * POST /api/device-token/test-send-notification
     * 
     * Không cần authentication - dùng để test
     */
    @PostMapping("/test-send-notification")
    ApiResponse<SendPushNotificationResponse> testSendNotification(
            @RequestBody SendPushNotificationRequest request) {
        log.info("📤 Test push notification request received (PUBLIC)");
        log.info("   Device Token: {}", request.getDeviceToken());
        log.info("   Title: {}", request.getTitle());
        log.info("   Body: {}", request.getBody());
        
        String messageId;
        
        // Nếu có data, gửi kèm custom data
        if (request.getDataKey() != null && request.getDataValue() != null) {
            messageId = firebaseMessagingService.sendNotificationWithData(
                request.getDeviceToken(),
                request.getTitle(),
                request.getBody(),
                request.getDataKey(),
                request.getDataValue()
            );
        } else {
            // Gửi notification đơn giản
            messageId = firebaseMessagingService.sendNotificationToDevice(
                request.getDeviceToken(),
                request.getTitle(),
                request.getBody()
            );
        }
        
        SendPushNotificationResponse response = SendPushNotificationResponse.builder()
            .messageId(messageId)
            .deviceToken(request.getDeviceToken())
            .status("success")
            .build();
        
        return ApiResponse.<SendPushNotificationResponse>builder()
            .result(response)
            .message("Test notification sent successfully")
            .build();
    }

    /**
     * API đẩy notification tới một thiết bị cụ thể (TEST)
     * POST /api/device-token/send-notification
     * 
     * Yêu cầu:
     * - deviceToken: FCM token của thiết bị (lấy từ /register response)
     * - title: Tiêu đề notification
     * - body: Nội dung notification
     * - dataKey (optional): Custom data key
     * - dataValue (optional): Custom data value
     * 
     * Ví dụ request:
     * {
     *   "deviceToken": "f7KP2wLh...",
     *   "title": "Đơn hàng mới",
     *   "body": "Bạn có một đơn hàng mới chờ xác nhận",
     *   "dataKey": "orderId",
     *   "dataValue": "ORD-123456"
     * }
     */
    @PostMapping("/send-notification")
    ApiResponse<SendPushNotificationResponse> sendTestNotification(
            @RequestBody SendPushNotificationRequest request) {
        log.info("📤 Test push notification request received");
        log.info("   Device Token: {}", request.getDeviceToken());
        log.info("   Title: {}", request.getTitle());
        log.info("   Body: {}", request.getBody());
        
        String messageId;
        
        // Nếu có data, gửi kèm custom data
        if (request.getDataKey() != null && request.getDataValue() != null) {
            messageId = firebaseMessagingService.sendNotificationWithData(
                request.getDeviceToken(),
                request.getTitle(),
                request.getBody(),
                request.getDataKey(),
                request.getDataValue()
            );
        } else {
            // Gửi notification đơn giản
            messageId = firebaseMessagingService.sendNotificationToDevice(
                request.getDeviceToken(),
                request.getTitle(),
                request.getBody()
            );
        }
        
        SendPushNotificationResponse response = SendPushNotificationResponse.builder()
            .messageId(messageId)
            .deviceToken(request.getDeviceToken())
            .status("success")
            .build();
        
        return ApiResponse.<SendPushNotificationResponse>builder()
            .result(response)
            .message("Notification sent successfully")
            .build();
    }

    /**
     * Helper: Lấy user ID từ JWT token
     */
    private String getCurrentUserId() {
        String userId = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();
        log.debug("Current user ID: {}", userId);
        return userId;
    }
}
