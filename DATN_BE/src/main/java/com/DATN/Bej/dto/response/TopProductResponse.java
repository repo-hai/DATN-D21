package com.DATN.Bej.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopProductResponse {
    
    List<TopProductItem> products;
    Integer limit;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TopProductItem {
        String productId;
        String productName;
        String productAttributeId;
        String productAttributeName;
        long totalSold;
        double totalRevenue;
        String image;
    }
}

