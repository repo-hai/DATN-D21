package com.DATN.Bej.event;

/**
 * Event được phát ra khi trạng thái đơn hàng được cập nhật
 */
public record OrderStatusUpdateEvent(
    String orderId,           // ID đơn hàng
    String userId,            // ID user sở hữu đơn hàng
    Integer oldStatus,        // Trạng thái cũ
    Integer newStatus,        // Trạng thái mới
    String statusName,        // Tên trạng thái
    String note               // Ghi chú (optional)
) {}

