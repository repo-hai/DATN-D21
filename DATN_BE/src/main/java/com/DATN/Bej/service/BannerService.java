package com.DATN.Bej.service;

import com.DATN.Bej.dto.response.BannerResponse;
import com.DATN.Bej.entity.Banner;
import com.DATN.Bej.mapper.BannerMapper;
import com.DATN.Bej.repository.BannerRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class BannerService {
    
    BannerRepository bannerRepository;
    BannerMapper bannerMapper;
    
    /**
     * Lấy tất cả banners đang active, sắp xếp theo displayOrder
     * @return Danh sách banners đang active
     */
    public List<BannerResponse> getActiveBanners() {
        log.info("📋 Getting all active banners");
        List<Banner> banners = bannerRepository.findAllActiveBannersOrderByDisplayOrder();
        log.info("✅ Found {} active banners", banners.size());
        return bannerMapper.toBannerResponseList(banners);
    }
    
    /**
     * Lấy tất cả banners (bao gồm cả inactive)
     * @return Danh sách tất cả banners
     */
    public List<BannerResponse> getAllBanners() {
        log.info("📋 Getting all banners");
        List<Banner> banners = bannerRepository.findAll();
        log.info("✅ Found {} banners", banners.size());
        return bannerMapper.toBannerResponseList(banners);
    }
}

