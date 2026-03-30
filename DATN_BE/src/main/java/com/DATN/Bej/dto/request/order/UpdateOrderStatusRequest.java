package com.DATN.Bej.dto.request.order;

import com.DATN.Bej.dto.request.cartRequest.OrderItemRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Request DTO để cập nhật trạng thái đơn hàng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateOrderStatusRequest {
    
    @NotNull(message = "Status is required")
    @Min(value = 0, message = "Status must be >= 0")
    @Max(value = 5, message = "Status must be <= 5")
    Integer status;  // Trạng thái mới của đơn hàng

    @NotNull(message = "Update note is required")
    String note;  // Ghi chú (optional)
}

