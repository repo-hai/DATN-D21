package com.DATN.Bej.mapper.product;

import com.DATN.Bej.dto.request.productRequest.ProductVariantRequest;
import com.DATN.Bej.dto.response.guest.ProductAttRes;
import com.DATN.Bej.dto.response.guest.ProductImgRes;
import com.DATN.Bej.dto.response.guest.ProductVariantRes;
import com.DATN.Bej.dto.response.productResponse.ProductVariantResponse;
import com.DATN.Bej.entity.product.ProductAttribute;
import com.DATN.Bej.entity.product.ProductImage;
import com.DATN.Bej.entity.product.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {

    @Mapping(target = "detailImages", ignore = true)
    ProductVariant toVariant(ProductVariantRequest request);

    //    @Mapping(source = "id", target = "id")
    @Mapping(source = "color", target = "color")
    @Mapping(source = "attributes", target = "attributes")
    ProductVariantResponse toVariantResponse(ProductVariant variant);
    
    @Mapping(source = "color", target = "color")
    @Mapping(source = "detailImages", target = "detailImages", qualifiedByName = "mapDetailImages")
    @Mapping(source = "attributes", target = "attributes", qualifiedByName = "mapAttributes")
    ProductVariantRes toVariantDetailRes(ProductVariant variant);
    
    List<ProductVariantRes> toVariantDetailResList(List<ProductVariant> variants);

    List<ProductVariantResponse> toVariantResponseList(List<ProductVariant> variants);

    default  List<String> mapImages(List<ProductImage> images) {
        return images != null ?
                images.stream().map(ProductImage::getUrl).collect(Collectors.toList())
                : null;
    }
    
    @Named("mapAttributes")
    default List<ProductAttRes> mapAttributes(List<ProductAttribute> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return List.of();
        }
        return attributes.stream()
                .map(attr -> ProductAttRes.builder()
                        .id(attr.getId())
                        .name(attr.getName())
                        .originalPrice(attr.getOriginalPrice())
                        .finalPrice(attr.getFinalPrice())
                        .discount(attr.getDiscount())
                        .build())
                .collect(Collectors.toList());
    }
    
    @Named("mapDetailImages")
    default List<ProductImgRes> mapDetailImages(List<ProductImage> images) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }
        return images.stream()
                .map(img -> ProductImgRes.builder()
                        .url(img.getUrl())
                        .build())
                .collect(Collectors.toList());
    }

//    default List<ProductAttributeResponse> mapAttributes(List<VariantAttributeValue> values){
//        return values != null ?
//                values.stream().map(v -> new ProductAttributeResponse(
//                        v.getAttributeValue().getAttribute().getName(),
//                        v.getAttributeValue().getValue()
//                ))
//                        .toList() : null;
//    }
}
