package com.DATN.Bej.dto.response.order;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

/**
 * Response DTO khi cập nhật trạng thái đơn hàng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderStatusUpdateResponse {
    
    String orderId;          // ID đơn hàng
    Integer oldStatus;       // Trạng thái cũ
    Integer newStatus;       // Trạng thái mới
    String statusName;       // Tên trạng thái (ví dụ: "Đã xác nhận", "Đang giao hàng")
    LocalDate updatedAt;     // Thời gian cập nhật
    String note;             // Ghi chú
    String message;          // Thông báo
}

