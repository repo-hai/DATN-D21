package com.DATN.Bej.dto.request.payment;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Request DTO cho tạo thanh toán VNPay
 * Chỉ cần orderId, backend sẽ tự động lấy totalPrice từ Orders
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePaymentRequest {
    
    @NotBlank(message = "Order ID is required")
    String orderId;  // ID của đơn hàng cần thanh toán
}