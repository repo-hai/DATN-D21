package com.DATN.Bej.dto.request.cartRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemsUpdateRequest {

    List<OrderItemRequest> items;

}
