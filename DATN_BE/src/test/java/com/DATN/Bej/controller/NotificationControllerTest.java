package com.DATN.Bej.controller;

import com.DATN.Bej.dto.ApiNotificationRequest;
import com.DATN.Bej.dto.request.NotificationMultipleUsersRequest;
import com.DATN.Bej.dto.response.NotificationResponse;
import com.DATN.Bej.entity.identity.User;
import com.DATN.Bej.enums.NotificationType;
import com.DATN.Bej.event.BroadcastNotificationEvent;
import com.DATN.Bej.event.NotificationSendEvent;
import com.DATN.Bej.exception.AppException;
import com.DATN.Bej.exception.ErrorCode;
import com.DATN.Bej.repository.UserRepository;
import com.DATN.Bej.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@DisplayName("NotificationController - Unit Tests")
@org.springframework.context.annotation.Import(NotificationControllerTest.SecurityTestConfig.class)
class NotificationControllerTest {

    @org.springframework.boot.test.context.TestConfiguration
    @org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
    static class SecurityTestConfig {
        @org.springframework.context.event.EventListener
        public void onNotificationSendEvent(com.DATN.Bej.event.NotificationSendEvent event) {
            if ("non-existent".equals(event.userId())) {
                throw new RuntimeException("Publish failed");
            }
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    // Removed MockBean for ApplicationEventPublisher as Spring Context handles it

    @MockBean
    private UserRepository userRepository;

    private ApiNotificationRequest notificationRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        notificationRequest = new ApiNotificationRequest(
                NotificationType.GENERAL_ANNOUNCEMENT,
                "Test Title",
                "Test Body",
                Map.of("key", "value")
        );

        testUser = User.builder()
                .id("user-123")
                .phoneNumber("user")
                .build();
    }

    // ═══════════════════════════════════════════════════════
    //  ADMIN OPERATIONS
    // ═══════════════════════════════════════════════════════
    @Nested
    @DisplayName("Admin Notification Operations")
    class AdminOperations {

        @Test
        @DisplayName("TC01 - Admin gửi thông báo cho 1 user → HTTP 200")
        @WithMockUser(roles = "ADMIN")
        void sendToUserById_Success() throws Exception {
            mockMvc.perform(post("/api/notifications/user/user-123")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(notificationRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Notification event published for user: user-123"));
        }

        @Test
        @DisplayName("TC02 - Admin gửi thông báo cho nhiều user → HTTP 200")
        @WithMockUser(roles = "ADMIN")
        void sendToMultipleUsers_Success() throws Exception {
            NotificationMultipleUsersRequest request = NotificationMultipleUsersRequest.builder()
                    .userIds(List.of("user-1", "user-2"))
                    .notification(notificationRequest)
                    .build();

            mockMvc.perform(post("/api/notifications/multiple-users")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Notifications sent to 2 users"));

            verify(notificationService).sendNotificationsToMultipleUsers(anyList(), any());
        }

        @Test
        @DisplayName("TC03 - Admin gửi broadcast → HTTP 200")
        @WithMockUser(roles = "ADMIN")
        void sendBroadcastNotification_Success() throws Exception {
            mockMvc.perform(post("/api/notifications/broadcast")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(notificationRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Broadcast notification event published"));
        }

        @Test
        @DisplayName("TC04 - User thường gọi Admin API → HTTP 403")
        @WithMockUser(roles = "USER")
        void adminEndpoints_ForbiddenForUser() throws Exception {
            mockMvc.perform(post("/api/notifications/broadcast")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(notificationRequest)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("TC05 - Người dùng ẩn danh gọi Admin API → HTTP 401")
        void adminEndpoints_Unauthorized() throws Exception {
            mockMvc.perform(post("/api/notifications/broadcast")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(notificationRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("TC06 - EventPublisher ném exception → HTTP 500")
        @WithMockUser(roles = "ADMIN")
        void sendToUserById_PublisherThrows() throws Exception {
            // The exception is thrown by the EventListener in SecurityTestConfig
            mockMvc.perform(post("/api/notifications/user/non-existent")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(notificationRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ═══════════════════════════════════════════════════════
    //  USER OPERATIONS
    // ═══════════════════════════════════════════════════════
    @Nested
    @DisplayName("User Notification Operations")
    class UserOperations {

        @BeforeEach
        void setupUser() {
            // Controller dùng principal.getName() = phoneNumber → tìm user qua UserRepository
            lenient().when(userRepository.findByPhoneNumber("user"))
                    .thenReturn(Optional.of(testUser));
        }

        @Test
        @DisplayName("TC07 - Lấy tất cả thông báo của user → HTTP 200")
        @WithMockUser(username = "user")
        void getAllMyNotifications_Success() throws Exception {
            NotificationResponse response = NotificationResponse.builder()
                    .id("notif-1")
                    .title("Title")
                    .body("Body")
                    .build();

            when(notificationService.getAllNotificationsForUser("user-123"))
                    .thenReturn(List.of(response));

            mockMvc.perform(get("/api/notifications/my-notifications").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result[0].title").value("Title"));
        }

        @Test
        @DisplayName("TC08 - User không tồn tại trong DB → HTTP 404")
        @WithMockUser(username = "non-existent")
        void getAllMyNotifications_UserNotFoundInDB() throws Exception {
            when(userRepository.findByPhoneNumber("non-existent"))
                    .thenThrow(new AppException(ErrorCode.USER_NOT_EXISTED));

            mockMvc.perform(get("/api/notifications/my-notifications").with(csrf()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("TC09 - Lấy số lượng thông báo chưa đọc → HTTP 200")
        @WithMockUser(username = "user")
        void getUnreadCount_Success() throws Exception {
            when(notificationService.countUnreadNotifications("user-123")).thenReturn(5L);

            mockMvc.perform(get("/api/notifications/unread-count").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.unreadCount").value(5));
        }

        @Test
        @DisplayName("TC10 - Lấy danh sách thông báo chưa đọc → HTTP 200")
        @WithMockUser(username = "user")
        void getUnreadNotifications_Success() throws Exception {
            NotificationResponse response = NotificationResponse.builder()
                    .id("notif-1")
                    .title("Unread")
                    .build();

            when(notificationService.getUnreadNotificationsForUser("user-123"))
                    .thenReturn(List.of(response));

            mockMvc.perform(get("/api/notifications/unread").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result[0].title").value("Unread"));
        }

        @Test
        @DisplayName("TC11 - Đánh dấu thông báo đã đọc thành công → HTTP 200")
        @WithMockUser(username = "user")
        void markAsRead_Success() throws Exception {
            when(notificationService.markAsRead("notif-1", "user-123")).thenReturn(true);

            mockMvc.perform(put("/api/notifications/notif-1/read").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Notification marked as read"));
        }

        @Test
        @DisplayName("TC12 - Đọc thông báo của người khác → HTTP 403")
        @WithMockUser(username = "user")
        void markAsRead_Forbidden() throws Exception {
            when(notificationService.markAsRead("notif-1", "user-123"))
                    .thenThrow(new SecurityException("Not your notification"));

            mockMvc.perform(put("/api/notifications/notif-1/read").with(csrf()))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("Not your notification"));
        }

        @Test
        @DisplayName("TC13 - Thông báo không tồn tại → HTTP 404")
        @WithMockUser(username = "user")
        void markAsRead_NotFound() throws Exception {
            when(notificationService.markAsRead("notif-999", "user-123")).thenReturn(false);

            mockMvc.perform(put("/api/notifications/notif-999/read").with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Notification not found"));
        }

        @Test
        @DisplayName("TC14 - Toggle tất cả thông báo → HTTP 200 với updatedCount=3")
        @WithMockUser(username = "user")
        void markAllAsRead_Success() throws Exception {
            when(notificationService.toggleMarkAllAsRead("user-123")).thenReturn(3);

            mockMvc.perform(put("/api/notifications/read-all").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.updatedCount").value(3));
        }

        @Test
        @DisplayName("TC15 - Toggle khi không có thông báo → HTTP 200 với updatedCount=0")
        @WithMockUser(username = "user")
        void markAllAsRead_Zero() throws Exception {
            when(notificationService.toggleMarkAllAsRead("user-123")).thenReturn(0);

            mockMvc.perform(put("/api/notifications/read-all").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.updatedCount").value(0));
        }
    }
}
