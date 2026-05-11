package com.DATN.Bej.service.payment;

import com.DATN.Bej.config.VNPayConfig;
import com.DATN.Bej.dto.response.payment.PaymentCallbackResponse;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class VNPayServiceTest {

    @Mock
    VNPayConfig vnPayConfig;
    @Mock
    OrderRepository orderRepository;
    @Mock
    ApplicationEventPublisher eventPublisher;
    @Mock
    HttpServletRequest request;

    @InjectMocks
    VNPayService vnPayService;

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
    }

    // ─────────────────────────────────────────────
    // TC-VNP-SER-001: createPayment thành công
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-VNP-SER-001 - createPayment success → PaymentResponse with paymentUrl")
    void createPayment_Success() {
        // Given
        when(orderRepository.findById("order-123")).thenReturn(Optional.of(order));
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);

        when(vnPayConfig.getTmnCode()).thenReturn("TMN_CODE");
        when(vnPayConfig.getReturnUrl()).thenReturn("http://return.url");
        when(vnPayConfig.getHashSecret()).thenReturn("SECRET");
        when(vnPayConfig.getPayUrl()).thenReturn("http://vnpay.url");

        // When
        PaymentResponse response = vnPayService.createPayment("order-123", 100000L, "Info", request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo("order-123");
        assertThat(response.getPaymentUrl()).contains("http://vnpay.url");
        verify(orderRepository).findById("order-123");
    }

    // ─────────────────────────────────────────────
    // TC-VNP-SER-002: createPayment - đơn hàng không tồn tại
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-VNP-SER-002 - createPayment order not found → AppException")
    void createPayment_OrderNotFound() {
        // Given
        when(orderRepository.findById("order-999")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> vnPayService.createPayment("order-999", 100000L, null, request))
                .isInstanceOf(AppException.class);
    }

    // ─────────────────────────────────────────────
    // TC-VNP-SER-003: handlePaymentCallback - thanh toán thành công
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-VNP-SER-003 - handlePaymentCallback success → status=2, event published")
    void handlePaymentCallback_Success() {
        try (var mockedConfig = mockStatic(VNPayConfig.class)) {
            // Given
            when(request.getParameter("vnp_ResponseCode")).thenReturn("00");
            when(request.getParameter("vnp_TransactionStatus")).thenReturn("00");
            when(request.getParameter("vnp_OrderInfo")).thenReturn("Thanh toan don hang order-123");
            when(request.getParameter("vnp_Amount")).thenReturn("10000000");
            when(request.getParameter("vnp_TxnRef")).thenReturn("txn-123");
            when(request.getParameter("vnp_SecureHash")).thenReturn("VALID_HASH");
            when(request.getParameter("vnp_TransactionNo")).thenReturn(null);
            when(request.getParameter("vnp_PayDate")).thenReturn(null);
            when(request.getParameterNames()).thenReturn(
                    Collections.enumeration(List.of("vnp_OrderInfo", "vnp_TransactionStatus", "vnp_ResponseCode"))
            );
            when(vnPayConfig.getHashSecret()).thenReturn("SECRET");
            when(orderRepository.findById("order-123")).thenReturn(Optional.of(order));

            mockedConfig.when(() -> VNPayConfig.hmacSHA512(anyString(), anyString())).thenReturn("VALID_HASH");

            // When
            PaymentCallbackResponse result = vnPayService.handlePaymentCallback(request);

            // Then
            assertThat(result.isSuccess()).isTrue();
            assertThat(order.getStatus()).isEqualTo(2);
            verify(orderRepository).save(order);
            verify(eventPublisher).publishEvent(any(OrderStatusUpdateEvent.class));
        }
    }

    // ─────────────────────────────────────────────
    // TC-VNP-SER-004: handlePaymentCallback - thanh toán thất bại (người dùng hủy)
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-VNP-SER-004 - handlePaymentCallback payment failed → status=3")
    void handlePaymentCallback_PaymentFailed() {
        try (var mockedConfig = mockStatic(VNPayConfig.class)) {
            // Given
            when(request.getParameter("vnp_ResponseCode")).thenReturn("24");
            when(request.getParameter("vnp_TransactionStatus")).thenReturn("02");
            when(request.getParameter("vnp_OrderInfo")).thenReturn("Thanh toan don hang order-123");
            when(request.getParameter("vnp_SecureHash")).thenReturn("VALID_HASH");
            when(request.getParameter("vnp_Amount")).thenReturn("10000000");
            when(request.getParameter("vnp_TxnRef")).thenReturn("txn-123");
            when(request.getParameter("vnp_TransactionNo")).thenReturn(null);
            when(request.getParameter("vnp_PayDate")).thenReturn(null);
            when(request.getParameterNames()).thenReturn(
                    Collections.enumeration(List.of("vnp_OrderInfo", "vnp_TransactionStatus"))
            );
            lenient().when(vnPayConfig.getHashSecret()).thenReturn("SECRET");
            when(orderRepository.findById("order-123")).thenReturn(Optional.of(order));

            mockedConfig.when(() -> VNPayConfig.hmacSHA512(anyString(), anyString())).thenReturn("VALID_HASH");

            // When
            PaymentCallbackResponse result = vnPayService.handlePaymentCallback(request);

            // Then
            assertThat(result.isSuccess()).isFalse();
            assertThat(order.getStatus()).isEqualTo(3);
            verify(orderRepository).save(order);
        }
    }

    // ─────────────────────────────────────────────
    // TC-VNP-SER-005: handlePaymentCallback - chữ ký không hợp lệ
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC-VNP-SER-005 - handlePaymentCallback invalid signature → not saved")
    void handlePaymentCallback_InvalidSignature() {
        try (var mockedConfig = mockStatic(VNPayConfig.class)) {
            // Given
            when(request.getParameter("vnp_SecureHash")).thenReturn("INVALID_HASH");
            when(request.getParameter("vnp_OrderInfo")).thenReturn("Thanh toan don hang order-123");
            when(request.getParameter("vnp_Amount")).thenReturn("10000000");
            when(request.getParameter("vnp_TxnRef")).thenReturn("txn-123");
            when(request.getParameter("vnp_TransactionNo")).thenReturn(null);
            when(request.getParameter("vnp_PayDate")).thenReturn(null);
            when(request.getParameterNames()).thenReturn(
                    Collections.enumeration(List.of("vnp_OrderInfo"))
            );
            when(vnPayConfig.getHashSecret()).thenReturn("SECRET");
            lenient().when(orderRepository.findById(anyString())).thenReturn(Optional.of(order));

            mockedConfig.when(() -> VNPayConfig.hmacSHA512(anyString(), anyString())).thenReturn("VALID_HASH");

            // When
            PaymentCallbackResponse result = vnPayService.handlePaymentCallback(request);

            // Then
            assertThat(result.getMessage()).isEqualTo("Invalid payment signature");
            assertThat(order.getStatus()).isEqualTo(0); // Không thay đổi
            verify(orderRepository, never()).save(any());
        }
    }
}
