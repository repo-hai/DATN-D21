package com.DATN.Bej.controller;

import com.DATN.Bej.dto.request.payment.CreatePaymentRequest;
import com.DATN.Bej.dto.response.payment.PaymentResponse;
import com.DATN.Bej.entity.cart.Orders;
import com.DATN.Bej.repository.product.OrderRepository;
import com.DATN.Bej.service.payment.VNPayService;
import com.DATN.Bej.service.payment.ZaloPayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@DisplayName("PaymentController - Unit Tests")
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VNPayService vnPayService;

    @MockBean
    private ZaloPayService zaloPayService;

    @MockBean
    private OrderRepository orderRepository;

    private CreatePaymentRequest createPaymentRequest;
    private Orders testOrder;

    @BeforeEach
    void setUp() {
        createPaymentRequest = CreatePaymentRequest.builder()
                .orderId("order-123")
                .build();

        testOrder = new Orders();
        testOrder.setId("order-123");
        testOrder.setStatus(2); // Đã thanh toán
        testOrder.setTotalPrice(100000.0);
    }

    // ─────────────────────────────────────────────
    // TC-PAY-CON-001: Tạo thanh toán ZaloPay thành công
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC01 - Tạo thanh toán ZaloPay thành công → HTTP 200")
    @WithMockUser
    void createZaloPayPayment_Success() throws Exception {
        PaymentResponse response = PaymentResponse.builder()
                .orderId("order-123")
                .amount(100000L)
                .orderUrl("https://zalopay.vn/pay/123")
                .paymentUrl("https://zalopay.vn/pay/123")
                .build();

        when(zaloPayService.createPayment(eq("order-123"), any())).thenReturn(response);

        mockMvc.perform(post("/payment/zalopay/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPaymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.orderUrl").value("https://zalopay.vn/pay/123"));
    }

    // ─────────────────────────────────────────────
    // TC-PAY-CON-002: Callback ZaloPay thành công → return_code = 1
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC02 - ZaloPay callback thành công → HTTP 200, return_code=1")
    @WithMockUser
    void zaloPayCallback_Success() throws Exception {
        Map<String, Object> callbackBody = new HashMap<>();
        callbackBody.put("data", "some-data");
        callbackBody.put("mac", "some-mac");

        when(zaloPayService.handleCallback(any())).thenReturn(true);

        mockMvc.perform(post("/payment/zalopay/callback")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(callbackBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.return_code").value(1))
                .andExpect(jsonPath("$.return_message").value("success"));
    }

    // ─────────────────────────────────────────────
    // TC-PAY-CON-003: Callback ZaloPay thất bại → return_code = -1
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC03 - ZaloPay callback thất bại → HTTP 200, return_code=-1")
    @WithMockUser
    void zaloPayCallback_Failure() throws Exception {
        when(zaloPayService.handleCallback(any())).thenReturn(false);

        mockMvc.perform(post("/payment/zalopay/callback")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new HashMap<>())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.return_code").value(-1))
                .andExpect(jsonPath("$.return_message").value("error"));
    }

    // ─────────────────────────────────────────────
    // TC-PAY-CON-004: Return với redirectUrl → HTTP 302
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC04 - ZaloPay return có redirectUrl → HTTP 302 redirect")
    @WithMockUser
    void zaloPayReturn_WithRedirect_Success() throws Exception {
        Map<String, Object> returnData = new HashMap<>();
        returnData.put("appTransId", "123456");
        returnData.put("status", 1);
        returnData.put("success", true);
        returnData.put("amount", 100000);

        when(zaloPayService.handleReturnCallback(any())).thenReturn(returnData);

        mockMvc.perform(get("/payment/zalopay/return")
                        .param("redirectUrl", "https://frontend.com/result"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location",
                        org.hamcrest.Matchers.containsString("https://frontend.com/result")));
    }

    // ─────────────────────────────────────────────
    // TC-PAY-CON-005: Return không có redirectUrl → HTTP 200 JSON
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC05 - ZaloPay return không có redirectUrl → HTTP 200 JSON")
    @WithMockUser
    void zaloPayReturn_NoRedirect_Success() throws Exception {
        Map<String, Object> returnData = new HashMap<>();
        returnData.put("appTransId", "123456");
        returnData.put("status", 1);
        returnData.put("success", true);
        returnData.put("amount", 100000);

        when(zaloPayService.handleReturnCallback(any())).thenReturn(returnData);

        mockMvc.perform(get("/payment/zalopay/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.success").value(true));
    }

    // ─────────────────────────────────────────────
    // TC-PAY-CON-006: Return callback xử lý thất bại → code 9999
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC06 - ZaloPay return callback null → HTTP 200, code=9999")
    @WithMockUser
    void zaloPayReturn_Failure() throws Exception {
        when(zaloPayService.handleReturnCallback(any())).thenReturn(null);

        mockMvc.perform(get("/payment/zalopay/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(9999));
    }

    // ─────────────────────────────────────────────
    // TC-PAY-CON-007: Lấy trạng thái thanh toán thành công
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC07 - Lấy trạng thái đơn hàng đã thanh toán → HTTP 200, isPaid=true")
    @WithMockUser
    void getPaymentStatus_Success() throws Exception {
        when(orderRepository.findById("order-123")).thenReturn(Optional.of(testOrder));

        mockMvc.perform(get("/payment/status/order-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.paid").value(true))
                .andExpect(jsonPath("$.result.statusName").value("Đã thanh toán"));
    }

    // ─────────────────────────────────────────────
    // TC-PAY-CON-008: Đơn hàng không tồn tại → HTTP 404
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("TC08 - Đơn hàng không tồn tại → HTTP 404")
    @WithMockUser
    void getPaymentStatus_NotFound() throws Exception {
        when(orderRepository.findById("non-existent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/payment/status/non-existent"))
                .andExpect(status().isNotFound());
    }
}
