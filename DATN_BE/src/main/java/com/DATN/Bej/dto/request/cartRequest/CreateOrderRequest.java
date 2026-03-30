package com.DATN.Bej.dto.request.cartRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOrderRequest {

    String userId;

    String phoneNumber;
    String email;
    String address;

    String description;

    int type;

    double totalPrice;
    List<OrderItemRequest> items;

}
