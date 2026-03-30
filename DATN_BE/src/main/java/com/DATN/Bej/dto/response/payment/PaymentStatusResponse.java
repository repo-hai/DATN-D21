package com.DATN.Bej.dto.response.payment;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Response DTO cho trạng thái thanh toán của đơn hàng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentStatusResponse {
    
    String orderId;
    int orderStatus;  // 0=pending, 1=processing, 2=paid, 3=payment_failed, 4=shipping, 5=completed
    String statusName;
    boolean isPaid;  // true nếu status = 2 hoặc 5
    Double totalPrice;
    String message;
}