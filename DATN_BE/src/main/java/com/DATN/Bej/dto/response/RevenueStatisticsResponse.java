package com.DATN.Bej.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenueStatisticsResponse {
    
    int year;
    
    Integer month;
    
    double totalRevenue;
    
    long totalOrders;

    long repairOrder;
    long saleOrder;
    
    List<MonthlyRevenue> monthlyRevenues;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class MonthlyRevenue {
        int month;
        String monthName;
        double revenue;
        long orderCount;
    }
}

