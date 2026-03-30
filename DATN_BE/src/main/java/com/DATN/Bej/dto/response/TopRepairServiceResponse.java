package com.DATN.Bej.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopRepairServiceResponse {
    
    List<TopRepairServiceItem> services;
    Integer limit;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TopRepairServiceItem {
        String serviceDescription;
        long usageCount;
        double totalRevenue;
    }
}

