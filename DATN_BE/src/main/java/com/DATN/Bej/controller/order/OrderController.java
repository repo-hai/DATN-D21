package com.DATN.Bej.controller.order;

import com.DATN.Bej.dto.request.ApiResponse;
import com.DATN.Bej.dto.response.cart.OrderDetailsResponse;
import com.DATN.Bej.dto.response.cart.OrdersResponse;
import com.DATN.Bej.dto.response.order.OrderStatusUpdateResponse;
import com.DATN.Bej.service.guest.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller xử lý các API liên quan đến đơn hàng của user
 * Tất cả endpoints đều yêu cầu authentication (JWT token)
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/orders")
public class OrderController {

    CartService cartService;

    /**
     * GET /orders/my-orders
     * Lấy danh sách tất cả đơn hàng của user hiện tại
     * 
     * @return List<OrderDetailsResponse> - Danh sách đơn hàng
     * 
     * User được xác định từ JWT token
     * Đơn hàng được sắp xếp theo thời gian tạo (mới nhất trước)
     */
    @GetMapping("/my-orders")
    ApiResponse<List<OrderDetailsResponse>> getMyOrders() {
        log.info("📦 Getting my orders");
        List<OrderDetailsResponse> result = cartService.getMyOrder();
        log.info("✅ Retrieved {} orders", result.size());
        return ApiResponse.<List<OrderDetailsResponse>>builder()
                .result(result)
                .build();
    }

    /**
     * GET /orders/{orderId}
     * Lấy chi tiết đơn hàng của user hiện tại
     * 
     * @param orderId ID của đơn hàng
     * @return OrderDetailsResponse - Chi tiết đơn hàng
     * 
     * Logic:
     * - Chỉ user sở hữu đơn hàng mới có thể xem chi tiết
     * - Bao gồm: thông tin đơn hàng, danh sách items, tổng tiền
     * 
     * Example:
     * - GET /orders/order-123 → Xem chi tiết đơn hàng có ID = order-123
     */
    @GetMapping("/{orderId}")
    ApiResponse<OrderDetailsResponse> getOrderDetails(@PathVariable String orderId) {
        log.info("📦 Getting order details - ID: {}", orderId);
        OrderDetailsResponse result = cartService.getMyOrderDetails(orderId);
        log.info("✅ Order details retrieved - ID: {}", orderId);
        return ApiResponse.<OrderDetailsResponse>builder()
                .result(result)
                .build();
    }

    @PutMapping("/repair-order/{orderId}/confirm")
    ApiResponse<OrdersResponse> confirmRepairOrder(@PathVariable String orderId){
        return ApiResponse.<OrdersResponse>builder()
                .result(cartService.confirmRepairOrder(orderId))
                .build();
    }
}

