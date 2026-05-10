package com.DATN.Bej.controller.cart;

import com.DATN.Bej.dto.request.cartRequest.CreateOrderRequest;
import com.DATN.Bej.dto.request.cartRequest.OrderItemRequest;
import com.DATN.Bej.dto.request.cartRequest.OrderItemsUpdateRequest;
import com.DATN.Bej.dto.request.order.UpdateOrderStatusRequest;
import com.DATN.Bej.dto.response.OrderStatisticsResponse;
import com.DATN.Bej.dto.response.RevenueStatisticsResponse;
import com.DATN.Bej.dto.response.TopProductResponse;
import com.DATN.Bej.dto.response.TopRepairServiceResponse;
import com.DATN.Bej.dto.response.WeeklyRevenueResponse;
import com.DATN.Bej.dto.response.cart.OrderDetailsResponse;
import com.DATN.Bej.dto.response.cart.OrdersResponse;
import com.DATN.Bej.dto.response.order.OrderStatusUpdateResponse;
import com.DATN.Bej.exception.AppException;
import com.DATN.Bej.exception.GlobalExceptionHandler;
import com.DATN.Bej.exception.ErrorCode;
import com.DATN.Bej.service.guest.CartService;
import com.DATN.Bej.service.order.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrdersManageControllerTest {

    @Mock
    private CartService cartService;
    @Mock
    private OrderService orderService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        OrdersManageController controller = new OrdersManageController(cartService, orderService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
        void createNewOrder_returnsApiResponse() throws Exception {
        // Test Case ID theo report: ITC-ORD-CTL-001

        // Arrange: request tạo đơn hợp lệ và service trả về chi tiết đơn đã tạo.
        CreateOrderRequest request = CreateOrderRequest.builder()
                .userId("USR-0001")
                .phoneNumber("0912345678")
                .address("12 Nguyễn Trãi")
                .items(List.of(OrderItemRequest.builder().productAttId("ATT-001").quantity(1).build()))
                .build();
        when(orderService.createNewOrder(any(CreateOrderRequest.class)))
                .thenReturn(OrderDetailsResponse.builder().id("ORD-20260412-001").build());

        // Act & Assert: gọi endpoint và kiểm tra API trả đúng id đơn hàng.
        mockMvc.perform(post("/manage/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.id").value("ORD-20260412-001"));
    }

    @Test
        void getAllOrders_returnsList() throws Exception {
        // Test Case ID theo report: ITC-ORD-CTL-002

        // Arrange: service trả về danh sách 2 đơn hàng cho admin.
        when(cartService.getAllOrders()).thenReturn(List.of(
                OrdersResponse.builder().id("ORD-01").build(),
                OrdersResponse.builder().id("ORD-02").build()
        ));

        // Act & Assert: endpoint trả đúng số phần tử trong mảng result.
        mockMvc.perform(get("/manage/orders/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.length()").value(2));
    }

    @Test
        void getOrdersByType_returnsFilteredOrders() throws Exception {
        // Test Case ID theo report: ITC-ORD-CTL-003

        // Arrange: service trả về danh sách đơn sửa chữa (type=1).
        when(cartService.getOrdersByType(1)).thenReturn(List.of(
                OrdersResponse.builder().id("ORD-REP-01").build()
        ));

        // Act & Assert: endpoint trả đúng đơn hàng lọc theo type.
        mockMvc.perform(get("/manage/orders/get-by-type").param("type", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.length()").value(1))
                .andExpect(jsonPath("$.result[0].id").value("ORD-REP-01"));
    }

    @Test
        void searchByPhoneOrName_returnsMatchingOrders() throws Exception {
        // Test Case ID theo report: ITC-ORD-CTL-004

        // Arrange: service trả về danh sách đơn khớp phoneNumber.
        when(cartService.getOrdersByPhoneOrName("0912345678")).thenReturn(List.of(
                OrdersResponse.builder().id("ORD-SEARCH-01").build()
        ));

        // Act & Assert: endpoint search trả đúng kết quả theo phoneNumber.
        mockMvc.perform(get("/manage/orders/search").param("phoneNumber", "0912345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.length()").value(1))
                .andExpect(jsonPath("$.result[0].id").value("ORD-SEARCH-01"));
    }

    @Test
        void getOrderDetails_returnsOrderDetails() throws Exception {
        // Test Case ID theo report: ITC-ORD-CTL-005

        // Arrange: service trả về chi tiết đơn tương ứng với orderId yêu cầu.
        when(cartService.getOrderDetails("ORD-DETAIL-01"))
                .thenReturn(OrderDetailsResponse.builder().id("ORD-DETAIL-01").build());

        // Act & Assert: endpoint trả đúng chi tiết đơn hàng.
        mockMvc.perform(get("/manage/orders/details/ORD-DETAIL-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value("ORD-DETAIL-01"));
    }

    @Test
        void updateOrderStatus_updatesStatus() throws Exception {
        // Test Case ID theo report: ITC-ORD-CTL-006

        // Arrange: request cập nhật status hợp lệ và service trả response nghiệp vụ.
        UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder().status(1).note("Đã xác nhận").build();
        when(orderService.updateOrderStatus(eq("ORD-STATUS-01"), any(UpdateOrderStatusRequest.class)))
                .thenReturn(OrderStatusUpdateResponse.builder()
                        .orderId("ORD-STATUS-01")
                        .oldStatus(0)
                        .newStatus(1)
                        .statusName("Đã xác nhận")
                        .build());

        // Act & Assert: endpoint trả response update status đúng dữ liệu.
        mockMvc.perform(put("/manage/orders/ORD-STATUS-01/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.orderId").value("ORD-STATUS-01"))
                .andExpect(jsonPath("$.result.newStatus").value(1));
    }

        @Test
        void updateOrderStatus_invalidStatus_returnsBadRequest() throws Exception {
                // Test Case ID theo report: ITC-ORD-CTL-006-1

                // Arrange: request co status vuot mien hop le de @Valid chan truoc khi goi service.
                UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder().status(6).note("Giá trị không hợp lệ").build();

                // Act & Assert: handler tra ve INVALID_KEY cho request khong hop le.
                mockMvc.perform(put("/manage/orders/ORD-STATUS-01/status")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_KEY.getCode()))
                                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_KEY.getMessage()));
        }

    @Test
        void updateOrderItems_returnsUpdatedOrderDetails() throws Exception {
        // Test Case ID theo report: ITC-ORD-CTL-007

        // Arrange: request cập nhật danh sách item và service trả về chi tiết đơn sau cập nhật.
        OrderItemsUpdateRequest request = OrderItemsUpdateRequest.builder()
                .items(List.of(OrderItemRequest.builder().productAttId("ATT-002").quantity(2).build()))
                .build();
        when(orderService.updateOrderItems(eq("ORD-ITEM-01"), any(OrderItemsUpdateRequest.class)))
                .thenReturn(OrderDetailsResponse.builder().id("ORD-ITEM-01").totalPrice(2_500_000D).build());

        // Act & Assert: endpoint trả đúng id đơn sau cập nhật item.
        mockMvc.perform(put("/manage/orders/ORD-ITEM-01/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value("ORD-ITEM-01"));
    }

    @Test
        void getRevenueStatistics_returnsRevenueStatistics() throws Exception {
        // Test Case ID theo report: ITC-ORD-CTL-008

        // Arrange: service trả về thống kê doanh thu theo tháng.
        when(orderService.getRevenueStatistics(2026, 4))
                .thenReturn(RevenueStatisticsResponse.builder().year(2026).month(4).totalRevenue(15_000_000D).build());

        // Act & Assert: endpoint trả đúng year/month trong response.
        mockMvc.perform(get("/manage/orders/revenue-statistics")
                        .param("year", "2026")
                        .param("month", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.year").value(2026))
                .andExpect(jsonPath("$.result.month").value(4));
    }

    @Test
        void getWeeklyRevenueStatistics_returnsWeeklyStatistics() throws Exception {
        // Test Case ID theo report: ITC-ORD-CTL-009

        // Arrange: service trả về thống kê tuần hợp lệ.
        when(orderService.getWeeklyRevenueStatistics(2026, 15))
                .thenReturn(WeeklyRevenueResponse.builder().year(2026).week(15).weekRange("07/04 - 13/04").build());

        // Act & Assert: endpoint trả đúng tuần cần thống kê.
        mockMvc.perform(get("/manage/orders/weekly-revenue-statistics")
                        .param("year", "2026")
                        .param("week", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.week").value(15));
    }

    @Test
        void getOrderStatistics_returnsOrderStatistics() throws Exception {
        // Test Case ID theo report: ITC-ORD-CTL-010

        // Arrange: service trả về thống kê đơn hàng mua và sửa chữa.
        when(orderService.getOrderStatistics(2026, 4, null))
                .thenReturn(OrderStatisticsResponse.builder().totalOrders(8).totalPurchaseOrders(5).totalRepairOrders(3).build());

        // Act & Assert: endpoint trả đúng tổng số đơn hàng.
        mockMvc.perform(get("/manage/orders/order-statistics")
                        .param("year", "2026")
                        .param("month", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.totalOrders").value(8));
    }

    @Test
        void getTopProducts_returnsTopProducts() throws Exception {
        // Test Case ID theo report: ITC-ORD-CTL-011

        // Arrange: service trả về danh sách top sản phẩm bán chạy.
        when(orderService.getTopProducts(2026, 4, 5))
                .thenReturn(TopProductResponse.builder()
                        .limit(5)
                        .products(List.of(TopProductResponse.TopProductItem.builder().productId("PROD-01").build()))
                        .build());

        // Act & Assert: endpoint trả đúng limit và có danh sách product.
        mockMvc.perform(get("/manage/orders/top-products")
                        .param("year", "2026")
                        .param("month", "4")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.limit").value(5))
                .andExpect(jsonPath("$.result.products.length()").value(1));
    }

    @Test
        void getTopRepairServices_returnsTopRepairServices() throws Exception {
        // Test Case ID theo report: ITC-ORD-CTL-012

        // Arrange: service trả về top dịch vụ sửa chữa.
        when(orderService.getTopRepairServices(2026, 4, 3))
                .thenReturn(TopRepairServiceResponse.builder()
                        .limit(3)
                        .services(List.of(TopRepairServiceResponse.TopRepairServiceItem.builder().serviceDescription("Thay pin").build()))
                        .build());

        // Act & Assert: endpoint trả đúng danh sách dịch vụ top.
        mockMvc.perform(get("/manage/orders/top-repair-services")
                        .param("year", "2026")
                        .param("month", "4")
                        .param("limit", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.limit").value(3))
                .andExpect(jsonPath("$.result.services.length()").value(1));
    }

    @Test
    void createNewOrder_serviceThrowsAccessDenied_returnsForbidden() throws Exception {
        // Test Case ID theo report: ITC-ORD-CTL-001-1

        // Arrange: service nem AccessDeniedException de mo phong user khong co quyen admin.
        CreateOrderRequest request = CreateOrderRequest.builder().userId("USR-0001").build();
        when(orderService.createNewOrder(any(CreateOrderRequest.class)))
                .thenThrow(new AccessDeniedException("denied"));

        // Act & Assert: handler phai tra 403 va code UNAUTHORIZED.
        mockMvc.perform(post("/manage/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getCode()));
    }

    @Test
    void getOrderDetails_serviceThrowsAppException_returnsNotFound() throws Exception {
        // Test Case ID theo report: ITC-ORD-CTL-005-1

        // Arrange: service nem loi khong ton tai order.
        when(cartService.getOrderDetails("ORD-404")).thenThrow(new AppException(ErrorCode.USER_NOT_EXISTED));

        // Act & Assert: response phai la 404 + message cua USER_NOT_EXISTED.
        mockMvc.perform(get("/manage/orders/details/ORD-404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_EXISTED.getCode()));
    }

    @Test
    void updateOrderStatus_serviceThrowsAccessDenied_returnsForbidden() throws Exception {
        // Test Case ID theo report: ITC-ORD-CTL-006-2

        // Arrange: service tu choi cap nhat khi khong du quyen.
        UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder().status(1).note("Đã xác nhận").build();
        when(orderService.updateOrderStatus(eq("ORD-STATUS-02"), any(UpdateOrderStatusRequest.class)))
                .thenThrow(new AccessDeniedException("denied"));

        // Act & Assert: endpoint tra 403 theo handler chung.
        mockMvc.perform(put("/manage/orders/ORD-STATUS-02/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getCode()));
    }

    @Test
    void updateOrderItems_serviceThrowsAppException_returnsBadRequest() throws Exception {
        // Test Case ID theo report: ITC-ORD-CTL-007-1

        // Arrange: service nem loi khi danh sach items rong.
        OrderItemsUpdateRequest request = OrderItemsUpdateRequest.builder().items(List.of()).build();
        when(orderService.updateOrderItems(eq("ORD-ITEM-02"), any(OrderItemsUpdateRequest.class)))
                .thenThrow(new AppException(ErrorCode.INVALID_KEY));

        // Act & Assert: handler tra 400 cho request khong hop le.
        mockMvc.perform(put("/manage/orders/ORD-ITEM-02/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_KEY.getCode()));
    }

    @Test
    void getRevenueStatistics_yearOnly_returnsYearlyStatistics() throws Exception {
        // Test Case ID theo report: ITC-ORD-CTL-008-1

        // Arrange: service tra thong ke ca nam khi month = null.
        when(orderService.getRevenueStatistics(2026, null))
                .thenReturn(RevenueStatisticsResponse.builder().year(2026).month(null).totalRevenue(99_000_000D).build());

        // Act & Assert: controller phai forward year-only request sang service.
        mockMvc.perform(get("/manage/orders/revenue-statistics").param("year", "2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.year").value(2026));
    }

    @Test
    void getTopProducts_nullLimit_defaultsHandledByService() throws Exception {
        // Test Case ID theo report: ITC-ORD-CTL-011-1

        // Arrange: service tra ve response limit mac dinh khi limit = null.
        when(orderService.getTopProducts(2026, 4, null))
                .thenReturn(TopProductResponse.builder().limit(10).products(List.of()).build());

        // Act & Assert: controller phai chuyen limit null xuong service.
        mockMvc.perform(get("/manage/orders/top-products")
                        .param("year", "2026")
                        .param("month", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.limit").value(10));
    }

    @Test
    void getTopRepairServices_noData_returnsEmptyList() throws Exception {
        // Test Case ID theo report: ITC-ORD-CTL-012-1

        // Arrange: service tra ve danh sach rong khi khong co du lieu.
        when(orderService.getTopRepairServices(2026, 4, 5))
                .thenReturn(TopRepairServiceResponse.builder().limit(5).services(List.of()).build());

        // Act & Assert: endpoint van tra OK voi danh sach rong.
        mockMvc.perform(get("/manage/orders/top-repair-services")
                        .param("year", "2026")
                        .param("month", "4")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.services.length()").value(0));
    }
}
