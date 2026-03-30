package com.DATN.Bej.dto.request.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Request DTO cho thanh toán VNPay
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentRequest {
    
    @NotBlank(message = "Order ID is required")
    String orderId;  // ID của đơn hàng cần thanh toán
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    Long amount;  // Số tiền thanh toán (VND)
    
    String orderInfo;  // Thông tin đơn hàng (optional, có thể lấy từ order)
    
    String returnUrl;  // URL trả về sau khi thanh toán (optional)
}

