package com.DATN.Bej.dto.response.payment;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Response DTO cho callback thanh toán VNPay
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentCallbackResponse {
    
    String orderId;              // ID đơn hàng
    String transactionRef;       // Mã tham chiếu giao dịch
    String transactionId;        // Mã giao dịch VNPay
    String paymentTime;          // Thời gian thanh toán
    Long amount;                 // Số tiền đã thanh toán
    int paymentStatus;           // Trạng thái: 1 = thành công, 0 = thất bại, -1 = lỗi
    String message;              // Thông báo
    boolean success;             // true nếu thanh toán thành công
}

