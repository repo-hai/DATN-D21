package com.DATN.Bej.service.payment;

import com.DATN.Bej.config.ZaloPayConfig;
import com.DATN.Bej.dto.response.payment.PaymentResponse;
import com.DATN.Bej.entity.cart.Orders;
import com.DATN.Bej.entity.identity.User;
import com.DATN.Bej.event.OrderStatusUpdateEvent;
import com.DATN.Bej.exception.AppException;
import com.DATN.Bej.repository.product.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZaloPayServiceTest {

    @Mock
    ZaloPayConfig zaloPayConfig;
    @Mock
    OrderRepository orderRepository;
    @Mock
    ApplicationEventPublisher eventPublisher;
    @Mock
    HttpServletRequest request;
    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    ZaloPayService zaloPayService;

    Orders order;
    User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id("user-123").build();
        order = new Orders();
        order.setId("order-123");
        order.setUser(user);
        order.setStatus(0);
        order.setTotalPrice(100000.0);

        // ZaloPayService có `restTemplate = new RestTemplate()` là final field không inject được.
        // Dùng ReflectionTestUtils để inject mock vào field.
        ReflectionTestUtils.setField(zaloPayService, "restTemplate", restTemplate);
    }

    // ─────────────────────────────────────────────
    // TC-ZAL-SER-001: createPayment thành công
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-ZAL-SER-001 - createPayment success → paymentUrl from ZaloPay")
    void createPayment_Success() {
        // Given
        when(orderRepository.findById("order-123")).thenReturn(Optional.of(order));
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getContextPath()).thenReturn("");

        when(zaloPayConfig.getAppId()).thenReturn("1234");
        when(zaloPayConfig.getKey1()).thenReturn("key1");
        when(zaloPayConfig.getCreateOrderUrl()).thenReturn("http://zalopay.url");
        when(zaloPayConfig.getCallbackUrl()).thenReturn("http://localhost/callback");

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("return_code", 1);
        mockResponse.put("order_url", "http://zalopay.url/pay");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        // When
        PaymentResponse response = zaloPayService.createPayment("order-123", request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getPaymentUrl()).isEqualTo("http://zalopay.url/pay");
    }

    // ─────────────────────────────────────────────
    // TC-ZAL-SER-002: createPayment - đơn hàng đã thanh toán
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-ZAL-SER-002 - createPayment already paid → AppException")
    void createPayment_OrderAlreadyPaid() {
        // Given
        order.setStatus(2); // Đã thanh toán
        when(orderRepository.findById("order-123")).thenReturn(Optional.of(order));

        // When & Then
        assertThatThrownBy(() -> zaloPayService.createPayment("order-123", request))
                .isInstanceOf(AppException.class);
    }

    // ─────────────────────────────────────────────
    // TC-ZAL-SER-003: createPayment - Gateway trả về lỗi
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-ZAL-SER-003 - createPayment ZaloPay API error → AppException")
    void createPayment_ApiError() {
        // Given
        when(orderRepository.findById("order-123")).thenReturn(Optional.of(order));
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getContextPath()).thenReturn("");

        when(zaloPayConfig.getAppId()).thenReturn("1234");
        when(zaloPayConfig.getKey1()).thenReturn("key1");
        when(zaloPayConfig.getCreateOrderUrl()).thenReturn("http://zalopay.url");
        when(zaloPayConfig.getCallbackUrl()).thenReturn("http://localhost/callback");

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("return_code", 2); // Lỗi từ ZaloPay
        mockResponse.put("return_message", "Invalid order");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        // When & Then
        assertThatThrownBy(() -> zaloPayService.createPayment("order-123", request))
                .isInstanceOf(AppException.class);
    }

    // ─────────────────────────────────────────────
    // TC-ZAL-SER-004: handleCallback - MAC không hợp lệ → false
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-ZAL-SER-004 - handleCallback invalid MAC → false")
    void handleCallback_InvalidMac() {
        // Given
        String data = "{\"app_trans_id\":\"txn-123\",\"amount\":100000,\"item\":\"[{\\\"itemid\\\":\\\"order-123\\\"}]\"}";
        String wrongMac = "WRONG_MAC";

        Map<String, Object> body = new HashMap<>();
        body.put("data", data);
        body.put("mac", wrongMac);

        when(zaloPayConfig.getKey2()).thenReturn("key2");

        // When
        boolean result = zaloPayService.handleCallback(body);

        // Then
        // HMAC thực tế sẽ không khớp với wrongMac → trả về false
        assertThat(result).isFalse();
        verify(orderRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────
    // TC-ZAL-SER-005: handleReturnCallback - thành công
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-ZAL-SER-005 - handleReturnCallback success → status=2, event published")
    void handleReturnCallback_Success() {
        // Given
        when(request.getParameterNames()).thenReturn(
                Collections.enumeration(List.of("apptransid", "status", "amount", "orderId"))
        );
        when(request.getParameter("apptransid")).thenReturn("txn-123");
        when(request.getParameter("status")).thenReturn("1");
        when(request.getParameter("amount")).thenReturn("100000");
        when(request.getParameter("orderId")).thenReturn("order-123");

        when(orderRepository.findById("order-123")).thenReturn(Optional.of(order));

        // When
        Map<String, Object> result = zaloPayService.handleReturnCallback(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("success")).isEqualTo(true);
        assertThat(order.getStatus()).isEqualTo(2);
        verify(orderRepository).save(order);
        verify(eventPublisher).publishEvent(any(OrderStatusUpdateEvent.class));
    }

    // ─────────────────────────────────────────────
    // TC-ZAL-SER-006: handleReturnCallback - đơn hàng không tồn tại
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-ZAL-SER-006 - handleReturnCallback order not found → null")
    void handleReturnCallback_OrderNotFound() {
        // Given - thiếu apptransid → method trả về null sớm
        when(request.getParameterNames()).thenReturn(
                Collections.enumeration(List.of("orderId"))
        );
        when(request.getParameter("orderId")).thenReturn("non-existent");
        // apptransid = null → handleReturnCallback trả về null

        // When
        Map<String, Object> result = zaloPayService.handleReturnCallback(request);

        // Then
        assertThat(result).isNull();
    }
}
