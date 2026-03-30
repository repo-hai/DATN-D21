package com.DATN.Bej.controller.cart;

import com.DATN.Bej.dto.request.ApiResponse;
import com.DATN.Bej.dto.request.cartRequest.CreateOrderRequest;
import com.DATN.Bej.dto.request.cartRequest.OrderItemsUpdateRequest;
import com.DATN.Bej.dto.request.cartRequest.OrderRequest;
import com.DATN.Bej.dto.request.order.UpdateOrderStatusRequest;
import com.DATN.Bej.dto.response.OrderStatisticsResponse;
import com.DATN.Bej.dto.response.RevenueStatisticsResponse;
import com.DATN.Bej.dto.response.TopProductResponse;
import com.DATN.Bej.dto.response.TopRepairServiceResponse;
import com.DATN.Bej.dto.response.WeeklyRevenueResponse;
import com.DATN.Bej.dto.response.cart.OrderDetailsResponse;
import com.DATN.Bej.dto.response.cart.OrdersResponse;
import com.DATN.Bej.dto.response.order.OrderStatusUpdateResponse;
import com.DATN.Bej.service.guest.CartService;
import com.DATN.Bej.service.order.OrderService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller quản lý đơn hàng cho Admin
 * Tất cả endpoints yêu cầu ROLE_ADMIN
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/manage/orders")
public class OrdersManageController {

    CartService cartService;
    OrderService orderService;


    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<OrderDetailsResponse> createNewOrder(@RequestBody @Valid CreateOrderRequest request){
        OrderDetailsResponse result = orderService.createNewOrder(request);
        log.info("✅ Order created successfully - Order ID: {}", result.getId());
        return ApiResponse.<OrderDetailsResponse>builder()
                .result(result)
                .build();
    }
    /**
     * GET /manage/orders/get-all
     * Lấy danh sách tất cả đơn hàng (Admin only)
     * Yêu cầu: ROLE_ADMIN
     */
    @GetMapping("/get-all")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<List<OrdersResponse>> getAllOrders(){
        log.info("📦 Admin getting all orders");
        return ApiResponse.<List<OrdersResponse>>builder()
                .result(cartService.getAllOrders())
                .build();
    }

    @GetMapping("/get-by-type")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<List<OrdersResponse>> getOrdersByType(@RequestParam int type){
        return ApiResponse.<List<OrdersResponse>>builder()
                .result(cartService.getOrdersByType(type))
                .build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<List<OrdersResponse>> getOrdersByPhoneOrName(@RequestParam(required = false) String phoneNumber){
        return ApiResponse.<List<OrdersResponse>>builder()
                .result(cartService.getOrdersByPhoneOrName(phoneNumber))
                .build();
    }

    /**
     * GET /manage/orders/details/{orderId}
     * Lấy chi tiết đơn hàng (Admin only)
     * Yêu cầu: ROLE_ADMIN
     */
    @GetMapping("/details/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<OrderDetailsResponse> getOrderDetails(@PathVariable String orderId){
        log.info("📦 Admin getting order details - ID: {}", orderId);
        return ApiResponse.<OrderDetailsResponse>builder()
                .result(cartService.getOrderDetails(orderId))
                .build();
    }
    
    /**
     * PUT /manage/orders/{orderId}/status
     * Cập nhật trạng thái đơn hàng (Admin only)
     * Cập nhật real-time qua WebSocket
     * Yêu cầu: ROLE_ADMIN
     * 
     * @param orderId ID đơn hàng
     * @param request UpdateOrderStatusRequest chứa status mới
     * @return OrderStatusUpdateResponse với thông tin cập nhật
     * 
     * Status codes:
     * - 0: Chờ xử lý
     * - 1: Đã xác nhận
     * - 2: Đã thanh toán
     * - 3: Thanh toán thất bại
     * - 4: Đang giao hàng
     * - 5: Đã hoàn thành
     * 
     * WebSocket sẽ broadcast đến:
     * - /topic/orders/{userId}/{orderId} - User sở hữu đơn hàng
     * - /topic/orders/admin - Admin dashboard
     */
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<OrderStatusUpdateResponse> updateOrderStatus(
            @PathVariable String orderId,
            @RequestBody @Valid UpdateOrderStatusRequest request) {
        log.info("📦 Admin updating order status - Order: {}, Status: {}", orderId, request.getStatus());
        
        OrderStatusUpdateResponse result = orderService.updateOrderStatus(orderId, request);
        
        log.info("✅ Order status updated - Order: {}, Status: {} -> {}", 
                orderId, result.getOldStatus(), result.getNewStatus());
        
        return ApiResponse.<OrderStatusUpdateResponse>builder()
                .result(result)
                .build();
    }

    @PutMapping("/{orderId}/items")
    ApiResponse<OrderDetailsResponse> updateOrderItems(@PathVariable String orderId, @RequestBody OrderItemsUpdateRequest request){
        return ApiResponse.<OrderDetailsResponse>builder()
                .result(orderService.updateOrderItems(orderId, request))
                .build();
    }
    
    /**
     * GET /manage/orders/revenue-statistics
     * Thống kê doanh thu theo tháng cho admin
     * Yêu cầu: ROLE_ADMIN
     * 
     * @param year Năm cần thống kê (bắt buộc)
     * @param month Tháng cần thống kê (1-12, tùy chọn, null nếu muốn thống kê cả năm)
     * @return RevenueStatisticsResponse với thông tin thống kê doanh thu
     * 
     * Ví dụ:
     * - GET /manage/orders/revenue-statistics?year=2024&month=12 → Thống kê tháng 12/2024
     * - GET /manage/orders/revenue-statistics?year=2024 → Thống kê cả năm 2024
     */
    @GetMapping("/revenue-statistics")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<RevenueStatisticsResponse> getRevenueStatistics(
            @RequestParam int year,
            @RequestParam(required = false) Integer month) {
        log.info("📊 Admin getting revenue statistics - Year: {}, Month: {}", year, month);
        
        RevenueStatisticsResponse result = orderService.getRevenueStatistics(year, month);
        
        log.info("✅ Revenue statistics retrieved - Year: {}, Month: {}, Total Revenue: {}, Total Orders: {}", 
                year, month, result.getTotalRevenue(), result.getTotalOrders());
        
        return ApiResponse.<RevenueStatisticsResponse>builder()
                .result(result)
                .build();
    }
    
    /**
     * GET /manage/orders/weekly-revenue-statistics
     * Thống kê doanh thu theo tuần cho admin
     * Yêu cầu: ROLE_ADMIN
     * 
     * @param year Năm cần thống kê (bắt buộc)
     * @param week Số tuần trong năm (1-53, bắt buộc)
     * @return WeeklyRevenueResponse với thông tin thống kê doanh thu theo tuần
     * 
     * Ví dụ:
     * - GET /manage/orders/weekly-revenue-statistics?year=2024&week=12 → Thống kê tuần 12 năm 2024
     */
    @GetMapping("/weekly-revenue-statistics")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<WeeklyRevenueResponse> getWeeklyRevenueStatistics(
            @RequestParam int year,
            @RequestParam int week) {
        log.info("📊 Admin getting weekly revenue statistics - Year: {}, Week: {}", year, week);
        
        WeeklyRevenueResponse result = orderService.getWeeklyRevenueStatistics(year, week);
        
        log.info("✅ Weekly revenue statistics retrieved - Year: {}, Week: {}, Total Revenue: {}, Total Orders: {}", 
                year, week, result.getTotalRevenue(), result.getTotalOrders());
        
        return ApiResponse.<WeeklyRevenueResponse>builder()
                .result(result)
                .build();
    }
    
    /**
     * GET /manage/orders/order-statistics
     * Thống kê số đơn mua bán và sửa chữa cho admin
     * Yêu cầu: ROLE_ADMIN
     * 
     * @param year Năm cần thống kê (tùy chọn)
     * @param month Tháng cần thống kê (1-12, tùy chọn)
     * @param week Tuần cần thống kê (1-53, tùy chọn, cần có year)
     * @return OrderStatisticsResponse với thông tin thống kê số đơn
     * 
     * Ví dụ:
     * - GET /manage/orders/order-statistics?year=2024&month=12 → Thống kê tháng 12/2024
     * - GET /manage/orders/order-statistics?year=2024&week=12 → Thống kê tuần 12 năm 2024
     * - GET /manage/orders/order-statistics?year=2024 → Thống kê cả năm 2024
     * - GET /manage/orders/order-statistics → Thống kê tất cả
     */
    @GetMapping("/order-statistics")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<OrderStatisticsResponse> getOrderStatistics(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer week) {
        log.info("📊 Admin getting order statistics - Year: {}, Month: {}, Week: {}", year, month, week);
        
        OrderStatisticsResponse result = orderService.getOrderStatistics(year, month, week);
        
        log.info("✅ Order statistics retrieved - Purchase Orders: {}, Repair Orders: {}, Total: {}", 
                result.getTotalPurchaseOrders(), result.getTotalRepairOrders(), result.getTotalOrders());
        
        return ApiResponse.<OrderStatisticsResponse>builder()
                .result(result)
                .build();
    }
    
    /**
     * GET /manage/orders/top-products
     * Thống kê các sản phẩm bán chạy nhất cho admin
     * Yêu cầu: ROLE_ADMIN
     * 
     * @param year Năm cần thống kê (tùy chọn)
     * @param month Tháng cần thống kê (1-12, tùy chọn)
     * @param limit Số lượng sản phẩm cần lấy (mặc định 10)
     * @return TopProductResponse với danh sách sản phẩm bán chạy nhất
     * 
     * Ví dụ:
     * - GET /manage/orders/top-products?year=2024&month=12&limit=5 → Top 5 sản phẩm tháng 12/2024
     * - GET /manage/orders/top-products?year=2024&limit=20 → Top 20 sản phẩm năm 2024
     */
    @GetMapping("/top-products")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<TopProductResponse> getTopProducts(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer limit) {
        log.info("📊 Admin getting top products - Year: {}, Month: {}, Limit: {}", year, month, limit);
        
        TopProductResponse result = orderService.getTopProducts(year, month, limit);
        
        log.info("✅ Top products retrieved - Count: {}", result.getProducts().size());
        
        return ApiResponse.<TopProductResponse>builder()
                .result(result)
                .build();
    }
    
    /**
     * GET /manage/orders/top-repair-services
     * Thống kê các dịch vụ sửa chữa được dùng nhiều nhất cho admin
     * Yêu cầu: ROLE_ADMIN
     * 
     * @param year Năm cần thống kê (tùy chọn)
     * @param month Tháng cần thống kê (1-12, tùy chọn)
     * @param limit Số lượng dịch vụ cần lấy (mặc định 10)
     * @return TopRepairServiceResponse với danh sách dịch vụ sửa chữa được dùng nhiều nhất
     * 
     * Ví dụ:
     * - GET /manage/orders/top-repair-services?year=2024&month=12&limit=5 → Top 5 dịch vụ tháng 12/2024
     * - GET /manage/orders/top-repair-services?year=2024&limit=20 → Top 20 dịch vụ năm 2024
     */
    @GetMapping("/top-repair-services")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<TopRepairServiceResponse> getTopRepairServices(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer limit) {
        log.info("📊 Admin getting top repair services - Year: {}, Month: {}, Limit: {}", year, month, limit);
        
        TopRepairServiceResponse result = orderService.getTopRepairServices(year, month, limit);
        
        log.info("✅ Top repair services retrieved - Count: {}", result.getServices().size());
        
        return ApiResponse.<TopRepairServiceResponse>builder()
                .result(result)
                .build();
    }

}
