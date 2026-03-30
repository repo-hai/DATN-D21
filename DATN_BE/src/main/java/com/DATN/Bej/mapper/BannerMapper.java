package com.DATN.Bej.mapper;

import com.DATN.Bej.dto.response.BannerResponse;
import com.DATN.Bej.entity.Banner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BannerMapper {
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "linkUrl", source = "linkUrl")
    @Mapping(target = "displayOrder", source = "displayOrder")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    BannerResponse toBannerResponse(Banner banner);
    
    List<BannerResponse> toBannerResponseList(List<Banner> banners);
}

