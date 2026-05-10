package com.DATN.Bej.mapper.product;

import com.DATN.Bej.dto.response.cart.OrderItemResponse;
import com.DATN.Bej.entity.cart.OrderItem;
import com.DATN.Bej.entity.product.Product;
import com.DATN.Bej.entity.product.ProductAttribute;
import com.DATN.Bej.entity.product.ProductImage;
import com.DATN.Bej.entity.product.ProductVariant;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMapperTest {

    private final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @Test
    void toOrderItemResponse_detailImagesExist_expectedFirstImageReturned() {
        // Test Case ID theo report: UTC-ORD-MAP-001

        // Arrange: order item co attribute, variant, product va danh sach anh chi tiet.
        Product product = new Product();
        product.setName("iPhone 15 Pro");

        ProductImage firstImage = new ProductImage();
        firstImage.setUrl("https://cdn.example.com/iphone-1.png");

        ProductImage secondImage = new ProductImage();
        secondImage.setUrl("https://cdn.example.com/iphone-2.png");

        ProductVariant variant = new ProductVariant();
        variant.setColor("Titan Xanh");
        variant.setProduct(product);
        variant.setDetailImages(List.of(firstImage, secondImage));

        ProductAttribute attribute = new ProductAttribute();
        attribute.setName("256GB");
        attribute.setVariant(variant);

        OrderItem orderItem = new OrderItem();
        orderItem.setProductA(attribute);
        orderItem.setQuantity(1);
        orderItem.setPrice(29_990_000D);

        // Act: map entity sang response DTO.
        OrderItemResponse response = orderMapper.toOrderItemResponse(orderItem);

        // Assert: mapper lấy đúng ảnh đầu tiên và map đầy đủ tên sản phẩm, màu sắc, thuộc tính.
        assertThat(response.getImg()).isEqualTo("https://cdn.example.com/iphone-1.png");
        assertThat(response.getProductName()).isEqualTo("iPhone 15 Pro");
        assertThat(response.getColor()).isEqualTo("Titan Xanh");
        assertThat(response.getProductAttName()).isEqualTo("256GB");
    }

    @Test
    void resolveImg_detailImagesMissing_expectedNullReturned() {
        // Test Case ID theo report: UTC-ORD-MAP-002

        // Arrange: variant khong co anh chi tiet.
        ProductVariant variant = new ProductVariant();
        variant.setColor("Đen");
        variant.setDetailImages(List.of());

        ProductAttribute attribute = new ProductAttribute();
        attribute.setName("128GB");
        attribute.setVariant(variant);

        OrderItem orderItem = new OrderItem();
        orderItem.setProductA(attribute);

        // Act: goi truc tiep ham resolveImg de kiem tra logic fallback.
        String imageUrl = orderMapper.resolveImg(orderItem);

        // Assert: truong anh phai null de tranh loi du lieu rong.
        assertThat(imageUrl).isNull();
    }
}
