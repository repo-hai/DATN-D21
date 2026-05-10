package com.DATN.Bej.service.order;

import com.DATN.Bej.dto.request.order.UpdateOrderStatusRequest;
import com.DATN.Bej.dto.request.cartRequest.OrderItemRequest;
import com.DATN.Bej.dto.request.cartRequest.OrderItemsUpdateRequest;
import com.DATN.Bej.dto.response.RevenueStatisticsResponse;
import com.DATN.Bej.dto.response.TopProductResponse;
import com.DATN.Bej.dto.response.order.OrderStatusUpdateResponse;
import com.DATN.Bej.entity.cart.Orders;
import com.DATN.Bej.entity.identity.User;
import com.DATN.Bej.event.OrderStatusUpdateEvent;
import com.DATN.Bej.exception.AppException;
import com.DATN.Bej.exception.ErrorCode;
import com.DATN.Bej.mapper.product.OrderMapper;
import com.DATN.Bej.repository.UserRepository;
import com.DATN.Bej.repository.product.CartItemRepository;
import com.DATN.Bej.repository.product.OrderItemRepository;
import com.DATN.Bej.repository.product.OrderRepository;
import com.DATN.Bej.repository.product.ProductAttributeRepository;
import com.DATN.Bej.entity.product.Product;
import com.DATN.Bej.entity.product.ProductAttribute;
import com.DATN.Bej.entity.product.ProductVariant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test cho {@link OrderService}.
 * Repository được mock để giữ đúng phạm vi unit test.
 * Các hành vi cần xác nhận dữ liệu thực sự được ghi/đọc trong DB nên được bổ sung bằng integration test
 * với {@code @SpringBootTest} hoặc {@code @DataJpaTest}; khi có transaction thì dùng rollback sau mỗi test.
 * CheckDB: không áp dụng trong class này vì repository đã bị mock hoàn toàn.
 * Rollback: không áp dụng trong class này vì test không ghi dữ liệu thật vào DB.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private ProductAttributeRepository productAttributeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUpSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("0909000111");
    }

    @AfterEach
    void tearDownSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateOrderStatus_updatesStatusAndPublishesEvent() {
        // Test Case ID theo report: UTC-ORD-SER-001

        // Arrange: chuan bi don hang hien tai, user thao tac va request doi trang thai sang "Da xac nhan".
        String orderId = "ORD-001";
        User owner = User.builder().id("USR-001").fullName("Nguyễn Văn A").phoneNumber("0909000111").build();
        Orders order = buildOrder(orderId, owner, 0);
        UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                .status(1)
                .note("Đã xác nhận đơn hàng")
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userRepository.findByPhoneNumber("0909000111")).thenReturn(Optional.of(owner));
        when(orderRepository.save(order)).thenReturn(order);

        // Act: goi service de cap nhat trang thai don hang.
        OrderStatusUpdateResponse response = orderService.updateOrderStatus(orderId, request);

        // Assert: kiem tra response, du lieu tren entity da thay doi dung va event/websocket duoc phat di.
        assertThat(response.getOrderId()).isEqualTo(orderId);
        assertThat(response.getOldStatus()).isEqualTo(0);
        assertThat(response.getNewStatus()).isEqualTo(1);
        assertThat(response.getStatusName()).isEqualTo("Đã xác nhận");
        assertThat(order.getStatus()).isEqualTo(1);
        assertThat(order.getUpdatedAt()).isEqualTo(LocalDate.now());
        assertThat(order.getOrderNotes()).hasSize(1);
        assertThat(order.getOrderNotes().getFirst().getNote()).isEqualTo("Đã xác nhận đơn hàng");

        ArgumentCaptor<Map<String, Object>> messageCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<OrderStatusUpdateEvent> eventCaptor = ArgumentCaptor.forClass(OrderStatusUpdateEvent.class);

        verify(orderRepository).save(order);
        verify(messagingTemplate).convertAndSend(eq("/topic/orders/USR-001/ORD-001"), messageCaptor.capture());
        verify(messagingTemplate).convertAndSend(eq("/topic/orders/admin"), any(Map.class));
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        assertThat(messageCaptor.getValue())
                .containsEntry("type", "ORDER_STATUS_UPDATE")
                .containsEntry("orderId", "ORD-001")
                .containsEntry("oldStatus", 0)
                .containsEntry("newStatus", 1)
                .containsEntry("statusName", "Đã xác nhận")
                .containsEntry("note", "Đã xác nhận đơn hàng");

        assertThat(eventCaptor.getValue().orderId()).isEqualTo("ORD-001");
        assertThat(eventCaptor.getValue().userId()).isEqualTo("USR-001");
        assertThat(eventCaptor.getValue().oldStatus()).isEqualTo(0);
        assertThat(eventCaptor.getValue().newStatus()).isEqualTo(1);
    }

    @Test
    void updateOrderStatus_throwsInvalidKeyForOutOfRangeStatus() {
        // Test Case ID theo report: UTC-ORD-SER-002

        // Arrange: chuan bi request voi status vuot ngoai mien hop le 0..5.
        String orderId = "ORD-002";
        User owner = User.builder().id("USR-002").fullName("Trần Thị B").phoneNumber("0909000111").build();
        Orders order = buildOrder(orderId, owner, 0);
        UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                .status(6)
                .note("Giá trị trạng thái không hợp lệ")
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userRepository.findByPhoneNumber("0909000111")).thenReturn(Optional.of(owner));

        // Act & Assert: xac nhan service chan request sai va nem dung ma loi nghiep vu.
        assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, request))
                .isInstanceOf(AppException.class)
                .extracting(ex -> ((AppException) ex).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_KEY);
    }

        @Test
        void updateOrderStatus_throwsUserNotExistedWhenOrderMissing() {
        // Test Case ID theo report: UTC-ORD-SER-001-1

        // Arrange: request hop le nhung orderId khong ton tai trong DB mock.
        String orderId = "ORD-404";
        UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
            .status(1)
            .note("Đã xác nhận đơn hàng")
            .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert: service phai nem loi chua ton tai order truoc khi xu ly cap nhat.
        assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, request))
            .isInstanceOf(AppException.class)
            .extracting(ex -> ((AppException) ex).getErrorCode())
            .isEqualTo(ErrorCode.USER_NOT_EXISTED);
        }

        @Test
        void updateOrderStatus_throwsInvalidKeyWhenStatusBelowRange() {
        // Test Case ID theo report: UTC-ORD-SER-001-2

        // Arrange: status = -1 la gia tri khong hop le.
        String orderId = "ORD-003";
        User owner = User.builder().id("USR-003").fullName("Lê Văn D").phoneNumber("0909000111").build();
        Orders order = buildOrder(orderId, owner, 0);
        UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
            .status(-1)
            .note("Trạng thái âm không hợp lệ")
            .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userRepository.findByPhoneNumber("0909000111")).thenReturn(Optional.of(owner));

        // Act & Assert: validation status phai chan truoc khi save du lieu.
        assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, request))
            .isInstanceOf(AppException.class)
            .extracting(ex -> ((AppException) ex).getErrorCode())
            .isEqualTo(ErrorCode.INVALID_KEY);
        }

    @Test
    void getRevenueStatistics_throwsInvalidKeyForInvalidMonth() {
        // Test Case ID theo report: UTC-ORD-SER-003

        // Arrange: thang 13 la gia tri bien khong hop le.
        int year = 2026;
        Integer month = 13;

        // Act & Assert: xac nhan service tu choi input sai truoc khi truy cap repository.
        assertThatThrownBy(() -> orderService.getRevenueStatistics(year, month))
                .isInstanceOf(AppException.class)
                .extracting(ex -> ((AppException) ex).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_KEY);
    }

    @Test
    void getRevenueStatistics_throwsInvalidKeyForMonthZero() {
        // Test Case ID theo report: UTC-ORD-SER-003-1

        // Arrange: month = 0 la gia tri bien khong hop le.
        int year = 2026;
        Integer month = 0;

        // Act & Assert: service phai chan month ngoai mien [1..12].
        assertThatThrownBy(() -> orderService.getRevenueStatistics(year, month))
                .isInstanceOf(AppException.class)
                .extracting(ex -> ((AppException) ex).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_KEY);
    }

    @Test
    void getRevenueStatistics_returnsMonthlyStatistics() {
        // Test Case ID theo report: UTC-ORD-SER-004

        // Arrange: mock du lieu tong hop doanh thu/so don de kiem tra logic ghep response.
        when(orderRepository.sumTotalPriceByOrderAtBetweenAndStatus(any(LocalDate.class), any(LocalDate.class))).thenReturn(25_000_000D);
        when(orderRepository.countByOrderAtBetween(any(LocalDate.class), any(LocalDate.class))).thenReturn(12L);
        when(orderRepository.countByOrderAtBetweenAndType(any(LocalDate.class), any(LocalDate.class), eq(1))).thenReturn(5L);
        when(orderRepository.countByOrderAtBetweenAndType(any(LocalDate.class), any(LocalDate.class), eq(0))).thenReturn(7L);

        // Act: lay thong ke doanh thu theo thang.
        RevenueStatisticsResponse response = orderService.getRevenueStatistics(2026, 4);

        // Assert: kiem tra tung truong thong ke duoc map dung tu du lieu repository.
        assertThat(response.getYear()).isEqualTo(2026);
        assertThat(response.getMonth()).isEqualTo(4);
        assertThat(response.getTotalRevenue()).isEqualTo(25_000_000D);
        assertThat(response.getTotalOrders()).isEqualTo(12L);
        assertThat(response.getRepairOrder()).isEqualTo(5L);
        assertThat(response.getSaleOrder()).isEqualTo(7L);
        assertThat(response.getMonthlyRevenues()).isNull();
    }

        @Test
        void getWeeklyRevenueStatistics_throwsInvalidKeyForWeekZero() {
        // Test Case ID theo report: ITC-ORD-SER-004-1

        // Arrange: week = 0 la gia tri khong hop le.
        int year = 2026;
        int week = 0;

        // Act & Assert: service phai chan week ngoai mien [1..53].
        assertThatThrownBy(() -> orderService.getWeeklyRevenueStatistics(year, week))
            .isInstanceOf(AppException.class)
            .extracting(ex -> ((AppException) ex).getErrorCode())
            .isEqualTo(ErrorCode.INVALID_KEY);
        }

        @Test
        void getTopProducts_nullLimit_defaultsToTen() {
        // Test Case ID theo report: ITC-ORD-SER-006-1

        // Arrange: repository tra 1 san pham top, limit = null phai duoc default thanh 10.
        Product product = new Product();
        product.setId("PROD-01");
        product.setName("iPhone 15");
        product.setImage("https://cdn.example.com/ip15.png");

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setColor("Black");

        ProductAttribute attribute = new ProductAttribute();
        attribute.setId("ATT-001");
        attribute.setName("128GB");
        attribute.setVariant(variant);

        when(orderItemRepository.findTopProductsByDateRange(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(List.<Object[]>of(new Object[]{"ATT-001", 3L, 3000000D}));
        when(productAttributeRepository.findById("ATT-001")).thenReturn(Optional.of(attribute));

        // Act: goi service voi limit null.
        TopProductResponse response = orderService.getTopProducts(2026, 4, null);

        // Assert: limit phai ve mac dinh 10 va tra dung san pham.
        assertThat(response.getLimit()).isEqualTo(10);
        assertThat(response.getProducts()).hasSize(1);
        assertThat(response.getProducts().get(0).getProductId()).isEqualTo("PROD-01");
        }

        @Test
        void createNewOrder_userNotFound_throwsUserNotExisted() {
        // Test Case ID theo report: UTC-ORD-SER-008-1

        // Arrange: request voi userId khong ton tai.
        var request = com.DATN.Bej.dto.request.cartRequest.CreateOrderRequest.builder()
            .userId("USR-404")
            .phoneNumber("0912345678")
            .address("Hà Nội")
            .items(List.of())
            .build();

        when(userRepository.findById("USR-404")).thenReturn(Optional.empty());

        // Act & Assert: service phai nem USER_NOT_EXISTED ngay tai buoc tim user.
        assertThatThrownBy(() -> orderService.createNewOrder(request))
            .isInstanceOf(AppException.class)
            .extracting(ex -> ((AppException) ex).getErrorCode())
            .isEqualTo(ErrorCode.USER_NOT_EXISTED);
        }

        @Test
        void updateOrderItems_productAttributeMissing_throwsUserNotExisted() {
        // Test Case ID theo report: ITC-ORD-SER-002-1

        // Arrange: order va user ton tai, nhung productAttId khong co trong DB mock.
        User owner = User.builder().id("USR-010").fullName("Nguyen Van A").phoneNumber("0909000111").build();
        Orders order = buildOrder("ORD-ITEM-404", owner, 0);
        OrderItemsUpdateRequest request = OrderItemsUpdateRequest.builder()
            .items(List.of(OrderItemRequest.builder().productAttId("ATT-UNKNOWN").quantity(1).build()))
            .build();

        when(orderRepository.findById("ORD-ITEM-404")).thenReturn(Optional.of(order));
        when(userRepository.findByPhoneNumber("0909000111")).thenReturn(Optional.of(owner));
        when(productAttributeRepository.findById("ATT-UNKNOWN")).thenReturn(Optional.empty());

        // Act & Assert: service phai nem loi khi khong tim thay product attribute.
        assertThatThrownBy(() -> orderService.updateOrderItems("ORD-ITEM-404", request))
            .isInstanceOf(AppException.class)
            .extracting(ex -> ((AppException) ex).getErrorCode())
            .isEqualTo(ErrorCode.USER_NOT_EXISTED);
        }

    private Orders buildOrder(String orderId, User user, int status) {
        Orders order = new Orders();
        order.setId(orderId);
        order.setUser(user);
        order.setStatus(status);
        order.setOrderItems(new ArrayList<>());
        order.setOrderNotes(new ArrayList<>());
        order.setTotalPrice(10_000_000D);
        return order;
    }
}
