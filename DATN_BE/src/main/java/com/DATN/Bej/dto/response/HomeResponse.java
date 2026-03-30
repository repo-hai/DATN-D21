package com.DATN.Bej.dto.response;

import com.DATN.Bej.dto.response.productResponse.ProductListResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HomeResponse {
    List<BannerResponse> banners;
    List<ProductListResponse> products;
}

