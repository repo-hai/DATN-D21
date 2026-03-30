package com.DATN.Bej.event;

/**
 * Event được phát ra khi một đơn hàng mới được tạo
 */
public record OrderCreatedEvent(
    String orderId,           // ID đơn hàng
    String userId,            // ID user tạo đơn hàng
    int orderType,            // Loại đơn hàng (0: mua bán, 1: sửa chữa)
    double totalPrice,        // Tổng giá trị đơn hàng
    String description        // Mô tả đơn hàng (optional)
) {}

