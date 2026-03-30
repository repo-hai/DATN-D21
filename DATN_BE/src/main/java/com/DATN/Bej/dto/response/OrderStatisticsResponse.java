package com.DATN.Bej.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderStatisticsResponse {
    
    long totalPurchaseOrders;
    long totalRepairOrders;
    long totalOrders;
    
    Integer year;
    Integer month;
    Integer week;
}

