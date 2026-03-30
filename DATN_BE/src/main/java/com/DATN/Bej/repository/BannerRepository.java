package com.DATN.Bej.repository;

import com.DATN.Bej.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, String> {
    
    /**
     * Lấy tất cả banners đang active, sắp xếp theo displayOrder
     */
    @Query("SELECT b FROM Banner b WHERE b.isActive = true ORDER BY b.displayOrder ASC")
    List<Banner> findAllActiveBannersOrderByDisplayOrder();
    
    /**
     * Lấy banners theo isActive status
     */
    List<Banner> findByIsActiveOrderByDisplayOrderAsc(Boolean isActive);
}

