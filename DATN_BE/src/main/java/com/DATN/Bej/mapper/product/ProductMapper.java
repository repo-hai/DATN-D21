package com.DATN.Bej.mapper.product;

import com.DATN.Bej.dto.request.productRequest.ProductRequest;
import com.DATN.Bej.dto.response.guest.ProductDetailRes;
import com.DATN.Bej.dto.response.productResponse.ProductListResponse;
import com.DATN.Bej.dto.response.productResponse.ProductResponse;
import com.DATN.Bej.dto.response.productResponse.VariantSummaryResponse;
import com.DATN.Bej.entity.product.Product;
import com.DATN.Bej.entity.product.ProductVariant;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductVariantMapper.class, VariantSummaryResponse.class})
public interface ProductMapper {
    //    @Mapping(target = "attributes", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "introImages", ignore = true)
    Product toProduct(ProductRequest request);

    //    @Mapping(source = "status", target = "status")
    @Mapping(source = "id", target = "id")
    ProductResponse toProductResponse(Product product);
    
    @Mapping(source = "variants", target = "variants")
    @Mapping(source = "category.id", target = "categoryId")
    ProductDetailRes toProductDetailResponse(Product product);
    List<ProductResponse> toResponseList(List<Product> products);


    @Mapping(
            target = "soldQuantity",
            expression = "java(product.getVariants().stream()" +
                    ".flatMap(v -> v.getAttributes().stream())" +
                    ".mapToInt(a -> a.getSoldQuantity()).sum())"
    )
    @Mapping(
            target = "stockQuantity",
            expression = "java(product.getVariants().stream()" +
                    ".flatMap(v -> v.getAttributes().stream())" +
                    ".mapToInt(a -> a.getStockQuantity()).sum())"
    )
    @Mapping(
            target = "originalPrice",
            expression = "java(product.getVariants().stream()" +
                    ".flatMap(v -> v.getAttributes().stream())" +
                    ".mapToDouble(a -> a.getOriginalPrice()).min().orElse(0))"
    )
    @Mapping(
            target = "finalPrice",
            expression = "java(product.getVariants().stream()" +
                    ".flatMap(v -> v.getAttributes().stream())" +
                    ".mapToDouble(a -> a.getFinalPrice()).min().orElse(0))"
    )
    ProductListResponse toProductListResponse(Product product);
    List<ProductListResponse> toListProduct(List<Product> products);

    default VariantSummaryResponse firstVariantSummary(List<ProductVariant> variants){
        if (variants == null || variants.isEmpty()) {
            return new VariantSummaryResponse(); // trả về object rỗng
        }
        ProductVariant v = variants.getFirst();
        VariantSummaryResponse summary = new VariantSummaryResponse();
        if(v != null && v.getDetailImages() != null && !v.getDetailImages().isEmpty()){
            summary.setOriginalPrice(v.getAttributes().getFirst().getOriginalPrice());
            summary.setFinalPrice(v.getAttributes().getFirst().getFinalPrice());
        }
//            summary.setThumbnail(v.getDetailImages().getFirst().getUrl());

        return summary;
    }

    @Mapping(target = "image", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "introImages", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProduct(@MappingTarget Product product, ProductRequest request);

    // Chuyển danh sách ProductAttribute thành danh sách String (chỉ lấy giá trị)
//    default List<String> mapSpecs(List<ProductAttribute> attributes) {
//        return attributes != null ?
//                attributes.stream().map(ProductAttribute::getValue).collect(Collectors.toList())
//                : null;
//    }
//
//    default  List<String> mapImages(List<ProductImage> images) {
//        return images != null ?
//                images.stream().map(ProductImage::getUrl).collect(Collectors.toList())
//                : null;
//    }
}

