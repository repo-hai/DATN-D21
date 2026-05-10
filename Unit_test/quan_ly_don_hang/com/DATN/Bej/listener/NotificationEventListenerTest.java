package com.DATN.Bej.listener;

import com.DATN.Bej.dto.ApiNotificationRequest;
import com.DATN.Bej.entity.identity.User;
import com.DATN.Bej.event.OrderCreatedEvent;
import com.DATN.Bej.event.OrderStatusUpdateEvent;
import com.DATN.Bej.repository.UserRepository;
import com.DATN.Bej.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationEventListenerTest {

    @Mock
    private NotificationService notificationService;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationEventListener notificationEventListener;

    @Test
    void handleOrderCreated_sendsPersonalAndAdminNotifications() {
        // Test Case ID theo report: UTC-NTF-LIS-001

        // Arrange: don hang moi duoc tao va he thong co 2 admin can nhan thong bao.
        OrderCreatedEvent event = new OrderCreatedEvent("ORD-3001", "USR-3001", 0, 12_500_000D, "Giao giờ hành chính");
        when(userRepository.findDistinctByRoles_NameIn(List.of("ADMIN")))
                .thenReturn(List.of(User.builder().id("ADM-01").build(), User.builder().id("ADM-02").build()));

        // Act: xu ly event tao don hang.
        notificationEventListener.handleOrderCreatedEvent(event);

        // Assert: user nhan thong bao ca nhan, admin nhan thong bao broadcast dung noi dung nghiep vu.
        ArgumentCaptor<ApiNotificationRequest> userCaptor = ArgumentCaptor.forClass(ApiNotificationRequest.class);
        ArgumentCaptor<ApiNotificationRequest> adminCaptor = ArgumentCaptor.forClass(ApiNotificationRequest.class);

        verify(notificationService).createAndSendPersonalNotification(eq("USR-3001"), userCaptor.capture());
        verify(notificationService).sendNotificationsToMultipleUsers(eq(List.of("ADM-01", "ADM-02")), adminCaptor.capture());

        assertThat(userCaptor.getValue().title()).contains("Đơn hàng đã được đặt thành công");
        assertThat(userCaptor.getValue().body()).contains("ORD-3001");
        assertThat(userCaptor.getValue().body()).contains("Giao giờ hành chính");

        assertThat(adminCaptor.getValue().title()).contains("Có đơn hàng mới cần xử lý");
        assertThat(adminCaptor.getValue().metadata()).containsEntry("userId", "USR-3001");
    }

    @Test
    void handleOrderCreated_sendsOnlyPersonalNotificationWhenNoAdmin() {
        // Test Case ID theo report: UTC-NTF-LIS-002

        // Arrange: khong co admin nao trong he thong tai thoi diem phat event.
        OrderCreatedEvent event = new OrderCreatedEvent("ORD-3002", "USR-3002", 1, 850_000D, null);
        when(userRepository.findDistinctByRoles_NameIn(List.of("ADMIN"))).thenReturn(List.of());

        // Act: xu ly event tao don hang.
        notificationEventListener.handleOrderCreatedEvent(event);

        // Assert: chi gui thong bao cho user, khong goi luong gui hang loat cho admin.
        verify(notificationService).createAndSendPersonalNotification(eq("USR-3002"), any(ApiNotificationRequest.class));
        verify(notificationService, never()).sendNotificationsToMultipleUsers(any(), any(ApiNotificationRequest.class));
    }

    @Test
    void handleOrderStatusUpdate_includesNoteInNotifications() {
        // Test Case ID theo report: UTC-NTF-LIS-003

        // Arrange: trang thai don doi sang "Dang giao hang" va co ghi chu giao hang.
        OrderStatusUpdateEvent event = new OrderStatusUpdateEvent(
                "ORD-3003",
                "USR-3003",
                1,
                4,
                "Đang giao hàng",
                "Tài xế sẽ giao trước 18:00"
        );
        when(userRepository.findDistinctByRoles_NameIn(List.of("ADMIN")))
                .thenReturn(List.of(User.builder().id("ADM-03").build()));

        // Act: xu ly event cap nhat trang thai don hang.
        notificationEventListener.handleOrderStatusUpdateEvent(event);

        // Assert: ca user va admin deu nhan payload co du status va ghi chu.
        ArgumentCaptor<ApiNotificationRequest> userCaptor = ArgumentCaptor.forClass(ApiNotificationRequest.class);
        ArgumentCaptor<ApiNotificationRequest> adminCaptor = ArgumentCaptor.forClass(ApiNotificationRequest.class);

        verify(notificationService).createAndSendPersonalNotification(eq("USR-3003"), userCaptor.capture());
        verify(notificationService).sendNotificationsToMultipleUsers(eq(List.of("ADM-03")), adminCaptor.capture());

        assertThat(userCaptor.getValue().title()).contains("Đơn hàng đang được giao hàng");
        assertThat(userCaptor.getValue().body()).contains("ORD-3003");
        assertThat(userCaptor.getValue().body()).contains("Tài xế sẽ giao trước 18:00");

        assertThat(adminCaptor.getValue().metadata())
                .containsEntry("orderId", "ORD-3003")
                .containsEntry("oldStatus", "1")
                .containsEntry("newStatus", "4")
                .containsEntry("statusName", "Đang giao hàng")
                .containsEntry("userId", "USR-3003");
    }
}
