package com.DATN.Bej.service;

import com.DATN.Bej.dto.ApiNotificationRequest;
import com.DATN.Bej.dto.NotificationPayload;
import com.DATN.Bej.dto.response.NotificationResponse;
import com.DATN.Bej.entity.Notification;
import com.DATN.Bej.entity.identity.User;
import com.DATN.Bej.enums.NotificationType;
import com.DATN.Bej.repository.NotificationRepository;
import com.DATN.Bej.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    SimpMessagingTemplate messagingTemplate;
    @Mock
    NotificationRepository notificationRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    FcmDeviceTokenService fcmDeviceTokenService;
    @Mock
    FirebaseMessagingService firebaseMessagingService;

    @InjectMocks
    NotificationService notificationService;

    User user;
    ApiNotificationRequest request;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id("user-123")
                .email("testuser@example.com")
                .build();

        request = new ApiNotificationRequest(
                NotificationType.GENERAL_ANNOUNCEMENT,
                "Test Title",
                "Test Body",
                Map.of("key", "value")
        );
    }

    // ─────────────────────────────────────────────
    // TC-NOT-SER-001: createAndSendPersonalNotification thành công
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-NOT-SER-001 - createAndSendPersonalNotification success → save + WebSocket + FCM")
    void createAndSendPersonalNotification_Success() {
        // Given
        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        when(fcmDeviceTokenService.getActiveTokensForUser("user-123")).thenReturn(List.of("token1", "token2"));
        
        // Mock save to assign ID
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification n = invocation.getArgument(0);
            n.setId(UUID.randomUUID().toString());
            return n;
        });

        // When
        notificationService.createAndSendPersonalNotification("user-123", request);

        // Then
        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/queue/notifications.user-123"), any(NotificationPayload.class));
        verify(firebaseMessagingService, times(2)).sendNotificationToDevice(anyString(), anyString(), anyString());
    }

    // ─────────────────────────────────────────────
    // TC-NOT-SER-002: createAndSendPersonalNotification - user không tồn tại
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-NOT-SER-002 - createAndSendPersonalNotification user not found → RuntimeException")
    void createAndSendPersonalNotification_UserNotFound() {
        // Given
        when(userRepository.findById("user-456")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> notificationService.createAndSendPersonalNotification("user-456", request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");

        verify(notificationRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────
    // TC-NOT-SER-003: sendBroadcast thành công
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-NOT-SER-003 - sendBroadcast success → save for each user + WebSocket topic")
    void sendBroadcast_Success() {
        // Given
        User user2 = User.builder().id("user-456").build();
        when(userRepository.findAll()).thenReturn(List.of(user, user2));
        when(fcmDeviceTokenService.getActiveTokensForUser(anyString())).thenReturn(Collections.emptyList());
        
        // Mock save to assign ID
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification n = invocation.getArgument(0);
            n.setId(UUID.randomUUID().toString());
            return n;
        });

        // When
        notificationService.sendBroadcast(request);

        // Then
        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/notifications"), any(NotificationPayload.class));
    }

    // ─────────────────────────────────────────────
    // TC-NOT-SER-004: getAllNotificationsForUser
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-NOT-SER-004 - getAllNotificationsForUser → mapped DTO list")
    void getAllNotificationsForUser_Success() {
        // Given
        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .recipient(user)
                .title("Title")
                .body("Body")
                .build();
        when(notificationRepository.findByRecipient_IdOrderByCreatedAtDesc("user-123"))
                .thenReturn(List.of(notification));

        // When
        List<NotificationResponse> result = notificationService.getAllNotificationsForUser("user-123");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Title");
    }

    // ─────────────────────────────────────────────
    // TC-NOT-SER-005: markAsRead thành công
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-NOT-SER-005 - markAsRead success → isRead=true, saved")
    void markAsRead_Success() {
        // Given
        Notification notification = Notification.builder()
                .id("notif-1")
                .recipient(user)
                .isRead(false)
                .build();
        when(notificationRepository.findById("notif-1")).thenReturn(Optional.of(notification));

        // When
        boolean result = notificationService.markAsRead("notif-1", "user-123");

        // Then
        assertThat(result).isTrue();
        assertThat(notification.isRead()).isTrue();
        verify(notificationRepository).save(notification);
    }

    // ─────────────────────────────────────────────
    // TC-NOT-SER-006: markAsRead - sai user → SecurityException
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-NOT-SER-006 - markAsRead wrong user → SecurityException")
    void markAsRead_SecurityException() {
        // Given
        User otherUser = User.builder().id("user-999").build();
        Notification notification = Notification.builder()
                .id("notif-1")
                .recipient(otherUser)
                .build();
        when(notificationRepository.findById("notif-1")).thenReturn(Optional.of(notification));

        // When & Then
        assertThatThrownBy(() -> notificationService.markAsRead("notif-1", "user-123"))
                .isInstanceOf(SecurityException.class);
    }

    // ─────────────────────────────────────────────
    // TC-NOT-SER-007: sendNotificationsToMultipleUsers thành công
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-NOT-SER-007 - sendNotificationsToMultipleUsers success → save ×2, send ×2")
    void sendNotificationsToMultipleUsers_Success() {
        // Given
        List<String> userIds = List.of("user-1", "user-2");
        when(userRepository.findById(anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);
            return Optional.of(User.builder().id(id).build());
        });
        when(fcmDeviceTokenService.getActiveTokensForUser(anyString())).thenReturn(Collections.emptyList());

        // Mock save to assign ID
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification n = invocation.getArgument(0);
            n.setId(UUID.randomUUID().toString());
            return n;
        });

        // When
        notificationService.sendNotificationsToMultipleUsers(userIds, request);

        // Then
        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(messagingTemplate, times(2)).convertAndSend(anyString(), any(NotificationPayload.class));
    }

    // ─────────────────────────────────────────────
    // TC-NOT-SER-008: toggleMarkAllAsRead → tất cả sang đã đọc
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-NOT-SER-008 - toggleMarkAllAsRead → all isRead=true, returns count")
    void toggleMarkAllAsRead_ToTrue() {
        // Given
        Notification n1 = Notification.builder().id("1").isRead(false).recipient(user).build();
        Notification n2 = Notification.builder().id("2").isRead(false).recipient(user).build();
        when(notificationRepository.findByRecipient_IdOrderByCreatedAtDesc("user-123"))
                .thenReturn(List.of(n1, n2));

        // When
        int result = notificationService.toggleMarkAllAsRead("user-123");

        // Then
        assertThat(result).isEqualTo(2);
        assertThat(n1.isRead()).isTrue();
        assertThat(n2.isRead()).isTrue();
        verify(notificationRepository).saveAll(anyList());
    }

    // ─────────────────────────────────────────────
    // TC-NOT-SER-009: countUnreadNotifications
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-NOT-SER-009 - countUnreadNotifications → 10")
    void countUnreadNotifications_Success() {
        // Given
        when(notificationRepository.countByRecipient_IdAndIsReadFalse("user-123")).thenReturn(10L);

        // When
        long result = notificationService.countUnreadNotifications("user-123");

        // Then
        assertThat(result).isEqualTo(10L);
    }

    // ─────────────────────────────────────────────
    // TC-NOT-SER-010: getUnreadNotificationsForUser
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-NOT-SER-010 - getUnreadNotificationsForUser → list unread")
    void getUnreadNotificationsForUser_Success() {
        // Given
        Notification n = Notification.builder().id("1").isRead(false).title("Unread").recipient(user).build();
        when(notificationRepository.findByRecipient_IdAndIsReadFalseOrderByCreatedAtDesc("user-123"))
                .thenReturn(List.of(n));

        // When
        List<NotificationResponse> result = notificationService.getUnreadNotificationsForUser("user-123");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Unread");
    }

    // ─────────────────────────────────────────────
    // TC-NOT-SER-011: sendNotificationsToMultipleUsers - 1 user không tồn tại
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-NOT-SER-011 - sendNotificationsToMultipleUsers partial failure → continue for valid users")
    void sendNotificationsToMultipleUsers_PartialFailure() {
        // Given
        List<String> userIds = List.of("user-1", "non-existent");
        when(userRepository.findById("user-1")).thenReturn(Optional.of(User.builder().id("user-1").build()));
        when(userRepository.findById("non-existent")).thenReturn(Optional.empty());
        when(fcmDeviceTokenService.getActiveTokensForUser("user-1")).thenReturn(Collections.emptyList());

        // When
        notificationService.sendNotificationsToMultipleUsers(userIds, request);

        // Then - chỉ lưu cho user-1, bỏ qua non-existent
        verify(notificationRepository, times(1)).save(any());
    }
}
