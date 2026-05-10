package com.DATN.Bej.service.payment;

import com.DATN.Bej.config.VNPayConfig;
import com.DATN.Bej.dto.response.payment.PaymentCallbackResponse;
import com.DATN.Bej.dto.response.payment.PaymentResponse;
import com.DATN.Bej.entity.cart.Orders;
import com.DATN.Bej.entity.identity.User;
import com.DATN.Bej.event.OrderStatusUpdateEvent;
import com.DATN.Bej.exception.AppException;
import com.DATN.Bej.exception.ErrorCode;
import com.DATN.Bej.repository.product.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit test cho {@link VNPayService}.
 * Repository và request đều được mock; việc kiểm tra callback cập nhật DB thực tế nên có integration test riêng
 * với {@code @SpringBootTest} và {@code @Transactional} để rollback sau mỗi test.
 * CheckDB: không áp dụng trong class này vì OrderRepository đã được mock.
 * Rollback: không áp dụng trong class này vì không có giao dịch DB thực.
 */
@ExtendWith(MockitoExtension.class)
class VNPayServiceTest {

    @Mock
    private VNPayConfig vnPayConfig;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private HttpServletRequest request;

    private VNPayService vnPayService;

    @BeforeEach
    void setUp() {
        vnPayService = spy(new VNPayService(vnPayConfig, orderRepository, eventPublisher));
    }

    @Test
    void createOrder_validInput_expectedUrlContainsRequiredParametersAndSecureHash() {
        // Test Case ID theo report: UTC-PAY-SER-001

        // Arrange: cau hinh VNPay day du va request co IP that de service tu build payment URL.
        when(vnPayConfig.getTmnCode()).thenReturn("TMNCODE01");
        when(vnPayConfig.getHashSecret()).thenReturn("SECRET123");
        when(vnPayConfig.getReturnUrl()).thenReturn("https://api.example.com/payment/vnpay-return");
        when(vnPayConfig.getPayUrl()).thenReturn("https://sandbox.vnpay.vn/paymentv2/vpcpay.html");
        when(request.getHeader("X-FORWARDED-FOR")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        // Act: goi thang ham build URL VNPay.
        String paymentUrl = vnPayService.createOrder(
                15_000_000,
                "Thanh toan don hang ORD-20260412-001",
                "https://api.example.com",
                request
        );

        // Assert: URL phai chua du tham so chinh va chu ky bao mat.
        assertThat(paymentUrl)
                .contains("https://sandbox.vnpay.vn/paymentv2/vpcpay.html?")
                .contains("vnp_TmnCode=TMNCODE01")
                .contains("vnp_Amount=1500000000")
                .contains("vnp_OrderInfo=Thanh+toan+don+hang+ORD-20260412-001")
                .contains("vnp_IpAddr=127.0.0.1")
                .contains("vnp_ReturnUrl=https%3A%2F%2Fapi.example.com%2Fpayment%2Fvnpay-return")
                .contains("vnp_SecureHash=");
    }

    @Test
    void createPayment_orderInfoNull_expectedDefaultOrderInfoAndTransactionRefReturned() {
        // Test Case ID theo report: UTC-PAY-SER-002

        // Arrange: don hang ton tai nhung orderInfo dau vao de null de kiem tra gia tri mac dinh.
        String orderId = "ORD-1001";
        Orders order = new Orders();
        order.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        doReturn("https://sandbox.vnpay.vn/pay?vnp_TxnRef=12345678&vnp_OrderInfo=Thanh+toan+don+hang+ORD-1001")
                .when(vnPayService)
                .createOrder(eq(1500000), eq("Thanh toan don hang ORD-1001"), eq("http://localhost:8080"), eq(request));

        // Act: tao payment URL tu service.
        PaymentResponse response = vnPayService.createPayment(orderId, 1_500_000L, null, request);

        // Assert: kiem tra service sinh dung paymentUrl, transactionRef va QR metadata.
        assertThat(response.getOrderId()).isEqualTo(orderId);
        assertThat(response.getAmount()).isEqualTo(1_500_000L);
        assertThat(response.getTransactionRef()).isEqualTo("12345678");
        assertThat(response.getPaymentUrl()).contains("vnp_TxnRef=12345678");
        assertThat(response.getQrCodeUrl()).isNotBlank();
        assertThat(response.getQrCodeData()).isEqualTo(response.getPaymentUrl());
    }

    @Test
    void createPayment_orderNotFound_expectedAppException() {
        // Test Case ID theo report: UTC-PAY-SER-003

        // Arrange: repository tra ve rong de mo phong orderId khong ton tai.
        when(orderRepository.findById("ORD-404")).thenReturn(Optional.empty());

        // Act & Assert: xac nhan service chan flow thanh toan voi order khong ton tai.
        assertThatThrownBy(() -> vnPayService.createPayment("ORD-404", 500_000L, "Thanh toán test", request))
                .isInstanceOf(AppException.class)
                .extracting(ex -> ((AppException) ex).getErrorCode())
                .isEqualTo(ErrorCode.USER_NOT_EXISTED);
    }

    @Test
    void orderReturn_validHashAndSuccessStatus_expectedOneReturned() {
        // Test Case ID theo report: UTC-PAY-SER-004

        // Arrange: dung du parameter callback va chu ky hop le tu VNPay.
        String hashSecret = "secret-key";
        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_Amount", "150000000");
        params.put("vnp_OrderInfo", "Thanh toan don hang ORD-2001");
        params.put("vnp_TxnRef", "87654321");
        params.put("vnp_TransactionStatus", "00");

        String hashData = "vnp_Amount=150000000&vnp_OrderInfo=Thanh toan don hang ORD-2001&vnp_TransactionStatus=00&vnp_TxnRef=87654321";
        String secureHash = VNPayConfig.hmacSHA512(hashSecret, hashData);

        when(vnPayConfig.getHashSecret()).thenReturn(hashSecret);
        mockRequestParameters(params, secureHash);

        // Act: xac thuc callback tra ve tu VNPay.
        int result = vnPayService.orderReturn(request);

        // Assert: callback hop le va trang thai giao dich "00" phai tra ve 1.
        assertThat(result).isEqualTo(1);
    }

    @Test
    void orderReturn_invalidHash_expectedMinusOneReturned() {
        // Test Case ID theo report: UTC-PAY-SER-005

        // Arrange: dung callback co chu ky sai de mo phong request bi gia mao.
        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_Amount", "9900000");
        params.put("vnp_OrderInfo", "Thanh toan don hang ORD-2002");
        params.put("vnp_TxnRef", "11223344");
        params.put("vnp_TransactionStatus", "00");

        when(vnPayConfig.getHashSecret()).thenReturn("secret-key");
        mockRequestParameters(params, "invalid-signature");

        // Act: xac thuc callback.
        int result = vnPayService.orderReturn(request);

        // Assert: chu ky sai phai tra ve -1 va khong duoc coi la giao dich hop le.
        assertThat(result).isEqualTo(-1);
    }

    @Test
    void handlePaymentCallback_invalidSignature_expectedFailureResponseWithoutSavingOrder() {
        // Test Case ID theo report: UTC-PAY-SER-006

        // Arrange: gia lap callback co chu ky khong hop le nhung order van ton tai trong he thong.
        User user = User.builder().id("USR-9001").fullName("Lê Văn C").build();
        Orders order = new Orders();
        order.setId("ORD-9001");
        order.setStatus(0);
        order.setUser(user);

        doReturn(-1).when(vnPayService).orderReturn(request);
        when(request.getParameter("vnp_OrderInfo")).thenReturn("Thanh toan don hang ORD-9001");
        when(request.getParameter("vnp_TxnRef")).thenReturn("99887766");
        when(request.getParameter("vnp_TransactionNo")).thenReturn("VNP-001");
        when(request.getParameter("vnp_PayDate")).thenReturn("20260412103030");
        when(request.getParameter("vnp_Amount")).thenReturn("2500000");
        when(orderRepository.findById("ORD-9001")).thenReturn(Optional.of(order));

        // Act: xu ly callback thanh toan.
        PaymentCallbackResponse response = vnPayService.handlePaymentCallback(request);

        // Assert: service phai tra ve failure response va khong publish event cap nhat trang thai.
        assertThat(response.getOrderId()).isEqualTo("ORD-9001");
        assertThat(response.getPaymentStatus()).isEqualTo(-1);
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Invalid payment signature");
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void handlePaymentCallback_successStatus_expectedOrderSavedAndEventPublished() {
        // Test Case ID theo report: UTC-PAY-SER-007

        // Arrange: gia lap callback thanh cong cho don hang chua thanh toan.
        User user = User.builder().id("USR-9002").fullName("Phạm Thị D").build();
        Orders order = new Orders();
        order.setId("ORD-9002");
        order.setStatus(0);
        order.setUser(user);

        doReturn(1).when(vnPayService).orderReturn(request);
        when(request.getParameter("vnp_OrderInfo")).thenReturn("Thanh toan don hang ORD-9002");
        when(request.getParameter("vnp_TxnRef")).thenReturn("44556677");
        when(request.getParameter("vnp_TransactionNo")).thenReturn("VNP-002");
        when(request.getParameter("vnp_PayDate")).thenReturn("20260412110000");
        when(request.getParameter("vnp_Amount")).thenReturn("3500000");
        when(orderRepository.findById("ORD-9002")).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        // Act: xu ly callback de cap nhat trang thai thanh toan.
        PaymentCallbackResponse response = vnPayService.handlePaymentCallback(request);

        // Assert: don hang duoc chuyen sang trang thai da thanh toan va event duoc publish.
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getPaymentStatus()).isEqualTo(1);
        assertThat(response.getMessage()).isEqualTo("Payment successful");
        assertThat(order.getStatus()).isEqualTo(2);

        ArgumentCaptor<OrderStatusUpdateEvent> eventCaptor = ArgumentCaptor.forClass(OrderStatusUpdateEvent.class);
        verify(orderRepository).save(order);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().orderId()).isEqualTo("ORD-9002");
        assertThat(eventCaptor.getValue().newStatus()).isEqualTo(2);
    }

    private void mockRequestParameters(Map<String, String> params, String secureHash) {
        Vector<String> names = new Vector<>(params.keySet());
        Enumeration<String> enumeration = names.elements();

        when(request.getParameterNames()).thenReturn(enumeration);
        params.forEach((key, value) -> when(request.getParameter(key)).thenReturn(value));
        when(request.getParameter("vnp_SecureHash")).thenReturn(secureHash);
    }
}
