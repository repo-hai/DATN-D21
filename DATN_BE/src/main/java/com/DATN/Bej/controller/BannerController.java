package com.DATN.Bej.controller;

import com.DATN.Bej.dto.request.ApiResponse;
import com.DATN.Bej.dto.response.BannerResponse;
import com.DATN.Bej.service.BannerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/banners")
public class BannerController {
    
    BannerService bannerService;
    
    /**
     * GET /banners
     * Lấy tất cả banners đang active, sắp xếp theo displayOrder
     * Public endpoint - không cần authentication
     */
    @GetMapping
    ApiResponse<List<BannerResponse>> getActiveBanners() {
        log.info("📋 Getting active banners");
        List<BannerResponse> banners = bannerService.getActiveBanners();
        return ApiResponse.<List<BannerResponse>>builder()
                .result(banners)
                .build();
    }
}

