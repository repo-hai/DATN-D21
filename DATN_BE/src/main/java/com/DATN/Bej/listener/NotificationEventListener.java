package com.DATN.Bej.listener;

import com.DATN.Bej.dto.ApiNotificationRequest;
import com.DATN.Bej.enums.NotificationType;
import com.DATN.Bej.event.BroadcastNotificationEvent;
import com.DATN.Bej.event.NotificationSendEvent;
import com.DATN.Bej.event.OrderCreatedEvent;
import com.DATN.Bej.event.OrderStatusUpdateEvent;
import com.DATN.Bej.repository.UserRepository;
import com.DATN.Bej.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Event Listener xử lý các sự kiện thông báo
 * Tự động gửi thông báo qua WebSocket, Firebase và lưu vào database
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    /**
     * Xử lý sự kiện gửi thông báo cá nhân
     * Tự động gửi qua WebSocket, Firebase và lưu vào database
     */
    @Async
    @EventListener
    public void handleNotificationSendEvent(NotificationSendEvent event) {
        log.info("📨 Handling NotificationSendEvent for user: {}", event.userId());
        try {
            notificationService.createAndSendPersonalNotification(
                event.userId(),
                event.request()
            );
            log.info("✅ Notification sent successfully to user: {}", event.userId());
        } catch (Exception e) {
            log.error("❌ Failed to send notification to user: {} - {}", event.userId(), e.getMessage(), e);
        }
    }

    /**
     * Xử lý sự kiện tạo đơn hàng mới
     * Tự động gửi thông báo cho user, admin và staff
     */
    @Async
    @EventListener
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("🛒 Handling OrderCreatedEvent - Order: {}, User: {}, Type: {}", 
                event.orderId(), event.userId(), event.orderType());
        
        try {
            String orderTypeName = event.orderType() == 0 ? "mua bán" : "sửa chữa";
            
            // Format giá tiền
            String formattedPrice = formatPrice(event.totalPrice());
            
            // Tạo thông báo cho user
            String userTitle = "Đơn hàng đã được đặt thành công";
            String userBody = String.format("Đơn hàng #%s (%s) của bạn đã được đặt thành công. Tổng giá trị: %s", 
                    event.orderId(), orderTypeName, formattedPrice);
            
            // Thêm mô tả nếu có
            if (event.description() != null && !event.description().trim().isEmpty()) {
                userBody += String.format(" - %s", event.description());
            }
            
            ApiNotificationRequest userNotificationRequest = new ApiNotificationRequest(
                NotificationType.ORDER_PLACED,
                userTitle,
                userBody,
                Map.of("orderId", event.orderId(), 
                       "orderType", String.valueOf(event.orderType()),
                       "totalPrice", String.valueOf(event.totalPrice()))
            );
            
            // Gửi thông báo cho user
            notificationService.createAndSendPersonalNotification(
                event.userId(),
                userNotificationRequest
            );
            
            // Tạo thông báo cho admin và staff
            String adminTitle = "Có đơn hàng mới cần xử lý";
            String adminBody = String.format("Có đơn hàng #%s (%s) mới cần xử lý với tổng giá trị: %s", 
                    event.orderId(), orderTypeName, formattedPrice);
            
            // Thêm mô tả nếu có
            if (event.description() != null && !event.description().trim().isEmpty()) {
                adminBody += String.format(" - %s", event.description());
            }
            
            ApiNotificationRequest adminNotificationRequest = new ApiNotificationRequest(
                NotificationType.ORDER_PLACED,
                adminTitle,
                adminBody,
                Map.of("orderId", event.orderId(), 
                       "orderType", String.valueOf(event.orderType()),
                       "userId", event.userId(),
                       "totalPrice", String.valueOf(event.totalPrice()))
            );
            
            // Lấy danh sách admin và staff
            List<String> adminAndStaffIds = userRepository.findDistinctByRoles_NameIn(List.of("ADMIN"))
                    .stream()
                    .map(user -> user.getId())
                    .collect(Collectors.toList());
            
            // Gửi thông báo cho admin và staff
            if (!adminAndStaffIds.isEmpty()) {
                notificationService.sendNotificationsToMultipleUsers(adminAndStaffIds, adminNotificationRequest);
                log.info("✅ Order created notification sent to {} admin/staff users", adminAndStaffIds.size());
            }
            
            log.info("✅ Order created notification sent to user: {}", event.userId());
        } catch (Exception e) {
            log.error("❌ Failed to send order created notification - Order: {}, User: {} - {}", 
                    event.orderId(), event.userId(), e.getMessage(), e);
        }
    }

    /**
     * Xử lý sự kiện cập nhật trạng thái đơn hàng
     * Tự động gửi thông báo cho user sở hữu đơn hàng, admin và staff
     */
    @Async
    @EventListener
    public void handleOrderStatusUpdateEvent(OrderStatusUpdateEvent event) {
        log.info("📦 Handling OrderStatusUpdateEvent - Order: {}, User: {}, Status: {} -> {}", 
                event.orderId(), event.userId(), event.oldStatus(), event.newStatus());
        
        try {
            // Tạo thông báo khác nhau tùy theo trạng thái mới
            String userTitle;
            String userBody;
            String adminTitle;
            String adminBody;
            
            int newStatus = event.newStatus();
            
            switch (newStatus) {
                case 0: // Chờ xử lý
                    userTitle = "Đơn hàng đang chờ xử lý";
                    userBody = String.format("Đơn hàng #%s của bạn đang chờ xử lý. Chúng tôi sẽ xử lý đơn hàng trong thời gian sớm nhất.", 
                            event.orderId());
                    adminTitle = "Đơn hàng đang chờ xử lý";
                    adminBody = String.format("Đơn hàng #%s đang chờ xử lý. Vui lòng xử lý đơn hàng.", event.orderId());
                    break;
                    
                case 1: // Đã xác nhận
                    userTitle = "Đơn hàng đã được xác nhận";
                    userBody = String.format("Đơn hàng #%s của bạn đã được xác nhận. Đơn hàng sẽ được chuẩn bị để giao hàng.", 
                            event.orderId());
                    adminTitle = "Đơn hàng đã được xác nhận";
                    adminBody = String.format("Đơn hàng #%s đã được xác nhận và sẽ được chuẩn bị giao hàng.", event.orderId());
                    break;
                    
                case 2: // Đã thanh toán
                    userTitle = "Đơn hàng đã được thanh toán";
                    userBody = String.format("Đơn hàng #%s của bạn đã được thanh toán thành công. Đơn hàng sẽ được chuẩn bị giao hàng.", 
                            event.orderId());
                    adminTitle = "Đơn hàng đã được thanh toán";
                    adminBody = String.format("Đơn hàng #%s đã được thanh toán thành công và sẽ được chuẩn bị giao hàng.", event.orderId());
                    break;
                    
                case 3: // Thanh toán thất bại
                    userTitle = "Thanh toán đơn hàng thất bại";
                    userBody = String.format("Thanh toán đơn hàng #%s của bạn không thành công. Vui lòng thử lại hoặc liên hệ hỗ trợ.", 
                            event.orderId());
                    adminTitle = "Thanh toán đơn hàng thất bại";
                    adminBody = String.format("Thanh toán đơn hàng #%s thất bại. Vui lòng kiểm tra và xử lý.", event.orderId());
                    break;
                    
                case 4: // Đang giao hàng
                    userTitle = "Đơn hàng đang được giao hàng";
                    userBody = String.format("Đơn hàng #%s của bạn đang được giao hàng. Vui lòng chuẩn bị nhận hàng.", 
                            event.orderId());
                    adminTitle = "Đơn hàng đang được giao hàng";
                    adminBody = String.format("Đơn hàng #%s đang được giao hàng.", event.orderId());
                    break;
                    
                case 5: // Đã hoàn thành
                    userTitle = "Đơn hàng đã hoàn thành";
                    userBody = String.format("Đơn hàng #%s của bạn đã hoàn thành. Cảm ơn bạn đã sử dụng dịch vụ!", 
                            event.orderId());
                    adminTitle = "Đơn hàng đã hoàn thành";
                    adminBody = String.format("Đơn hàng #%s đã hoàn thành.", event.orderId());
                    break;
                    
                default:
                    userTitle = "Cập nhật đơn hàng";
                    userBody = String.format("Đơn hàng #%s đã được cập nhật: %s", 
                            event.orderId(), event.statusName());
                    adminTitle = "Cập nhật đơn hàng";
                    adminBody = String.format("Đơn hàng #%s đã được cập nhật: %s", 
                            event.orderId(), event.statusName());
                    break;
            }
            
            // Thêm ghi chú nếu có
            if (event.note() != null && !event.note().trim().isEmpty()) {
                userBody += String.format(" - %s", event.note());
                adminBody += String.format(" - %s", event.note());
            }
            
            // Tạo thông báo cho user
            ApiNotificationRequest userNotificationRequest = new ApiNotificationRequest(
                NotificationType.ORDER_STATUS_UPDATE,
                userTitle,
                userBody,
                Map.of("orderId", event.orderId(), 
                       "oldStatus", String.valueOf(event.oldStatus()),
                       "newStatus", String.valueOf(event.newStatus()),
                       "statusName", event.statusName())
            );
            
            // Gửi thông báo cho user
            notificationService.createAndSendPersonalNotification(
                event.userId(),
                userNotificationRequest
            );
            
            // Tạo thông báo cho admin và staff
            ApiNotificationRequest adminNotificationRequest = new ApiNotificationRequest(
                NotificationType.ORDER_STATUS_UPDATE,
                adminTitle,
                adminBody,
                Map.of("orderId", event.orderId(), 
                       "oldStatus", String.valueOf(event.oldStatus()),
                       "newStatus", String.valueOf(event.newStatus()),
                       "statusName", event.statusName(),
                       "userId", event.userId())
            );
            
            // Lấy danh sách admin và staff
            List<String> adminAndStaffIds = userRepository.findDistinctByRoles_NameIn(List.of("ADMIN"))
                    .stream()
                    .map(user -> user.getId())
                    .collect(Collectors.toList());
            
            // Gửi thông báo cho admin và staff
            if (!adminAndStaffIds.isEmpty()) {
                notificationService.sendNotificationsToMultipleUsers(adminAndStaffIds, adminNotificationRequest);
                log.info("✅ Order status update notification sent to {} admin/staff users", adminAndStaffIds.size());
            }
            
            log.info("✅ Order status update notification sent to user: {}", event.userId());
        } catch (Exception e) {
            log.error("❌ Failed to send order status update notification - Order: {}, User: {} - {}", 
                    event.orderId(), event.userId(), e.getMessage(), e);
        }
    }
    
    /**
     * Format giá tiền với dấu phẩy ngăn cách hàng nghìn
     */
    private String formatPrice(double price) {
        return String.format("%,.0f VNĐ", price);
    }

    /**
     * Xử lý sự kiện broadcast thông báo
     * Gửi thông báo cho tất cả users trong hệ thống
     */
    @Async
    @EventListener
    public void handleBroadcastNotificationEvent(BroadcastNotificationEvent event) {
        log.info("📢 Handling BroadcastNotificationEvent - Title: {}", event.request().title());
        
        try {
            notificationService.sendBroadcast(event.request());
            log.info("✅ Broadcast notification sent successfully");
        } catch (Exception e) {
            log.error("❌ Failed to send broadcast notification - {}", e.getMessage(), e);
        }
    }
}