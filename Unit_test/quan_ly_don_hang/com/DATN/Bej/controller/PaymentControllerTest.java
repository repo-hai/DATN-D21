package com.DATN.Bej.controller;

import com.DATN.Bej.dto.request.payment.CreatePaymentRequest;
import com.DATN.Bej.dto.response.payment.PaymentResponse;
import com.DATN.Bej.entity.cart.Orders;
import com.DATN.Bej.exception.GlobalExceptionHandler;
import com.DATN.Bej.repository.product.OrderRepository;
import com.DATN.Bej.service.payment.VNPayService;
import com.DATN.Bej.service.payment.ZaloPayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private VNPayService vnPayService;
    @Mock
    private ZaloPayService zaloPayService;
    @Mock
    private OrderRepository orderRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        PaymentController controller = new PaymentController(vnPayService, zaloPayService, orderRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
        void createZaloPayPayment_returnsPaymentResponse() throws Exception {
        // Test Case ID theo report: ITC-PAY-CTL-001

        // Arrange: service ZaloPay tra ve payment response hop le.
        CreatePaymentRequest request = CreatePaymentRequest.builder().orderId("ORD-ZP-01").build();
        when(zaloPayService.createPayment(any(), any()))
                .thenReturn(PaymentResponse.builder().orderId("ORD-ZP-01").orderUrl("https://pay.zalo/abc").amount(15_000_000L).build());

        // Act & Assert: endpoint tra dung orderId va orderUrl.
        mockMvc.perform(post("/payment/zalopay/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.orderId").value("ORD-ZP-01"))
                .andExpect(jsonPath("$.result.orderUrl").value("https://pay.zalo/abc"));
    }

    @Test
        void zaloPayCallback_returnsSuccessCode() throws Exception {
        // Test Case ID theo report: ITC-PAY-CTL-002

        // Arrange: callback server-to-server duoc service xu ly thanh cong.
        when(zaloPayService.handleCallback(any())).thenReturn(true);

        // Act & Assert: endpoint raw callback tra return_code=1.
        mockMvc.perform(post("/payment/zalopay/callback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"data\":\"abc\",\"mac\":\"valid\",\"type\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.return_code").value(1))
                .andExpect(jsonPath("$.return_message").value("success"));
    }

    @Test
        void zaloPayReturn_redirectsToFrontendWithParams() throws Exception {
        // Test Case ID theo report: ITC-PAY-CTL-003

        // Arrange: service tra du lieu callback de controller redirect ve frontend.
        when(zaloPayService.handleReturnCallback(any()))
                .thenReturn(Map.of("appTransId", "260412_001", "status", 1, "success", true, "amount", 15000000));

        // Act & Assert: endpoint tra 302 va header Location co du query params.
        mockMvc.perform(get("/payment/zalopay/return")
                        .param("redirectUrl", "https://frontend.example.com/payment-result"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://frontend.example.com/payment-result?appTransId=260412_001&status=1&success=true&amount=15000000"));
    }

    @Test
        void getPaymentStatus_returnsPaidTrueForPaidOrder() throws Exception {
        // Test Case ID theo report: ITC-PAY-CTL-004

        // Arrange: repository tra don da thanh toan voi status=2.
        Orders order = new Orders();
        order.setId("ORD-PAY-01");
        order.setStatus(2);
        order.setTotalPrice(30_990_000D);
        when(orderRepository.findById("ORD-PAY-01")).thenReturn(Optional.of(order));

        // Act & Assert: endpoint tra trang thai da thanh toan.
        mockMvc.perform(get("/payment/status/ORD-PAY-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.orderId").value("ORD-PAY-01"))
                .andExpect(jsonPath("$.result.paid").value(true))
                .andExpect(jsonPath("$.result.orderStatus").value(2));
    }
}
