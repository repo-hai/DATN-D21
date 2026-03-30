package com.DATN.Bej.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WeeklyRevenueResponse {
    
    int year;
    int week;
    String weekRange;
    double totalRevenue;
    long totalOrders;
    
    List<DailyRevenue> dailyRevenues;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DailyRevenue {
        int day;
        String date;
        double revenue;
        long orderCount;
    }
}

