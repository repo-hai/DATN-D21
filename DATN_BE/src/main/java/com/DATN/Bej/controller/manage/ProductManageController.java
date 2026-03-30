package com.DATN.Bej.controller.manage;

import com.DATN.Bej.dto.request.ApiResponse;
import com.DATN.Bej.dto.request.productRequest.ProductRequest;
import com.DATN.Bej.dto.response.productResponse.ProductListResponse;
import com.DATN.Bej.dto.response.productResponse.ProductResponse;
import com.DATN.Bej.service.ProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/manage/product")
public class ProductManageController {

    ProductService productService;

    /**
     * GET /manage/product/list
     * Lấy danh sách tất cả products (kể cả inactive)
     * Yêu cầu: ROLE_ADMIN
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<List<ProductListResponse>> getAllProducts() {
        log.info("📋 Admin getting all products");
        return ApiResponse.<List<ProductListResponse>>builder()
                .result(productService.getAllProducts())
                .build();
    }
    
    /**
     * GET /manage/product/{productId}
     * Lấy chi tiết product
     * Yêu cầu: ROLE_ADMIN
     */
    @GetMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<ProductResponse> getProductDetails(@PathVariable String productId){
        log.info("📋 Admin getting product details: {}", productId);
        return ApiResponse.<ProductResponse>builder()
                .result(productService.getProductDetails(productId))
                .build();
    }
    
    /**
     * PUT /manage/product/update/{productId}
     * Cập nhật product
     * Yêu cầu: ROLE_ADMIN
     */
    @PutMapping("/update/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<ProductResponse> updateProduct(@PathVariable String productId, @ModelAttribute ProductRequest request) throws IOException {
        log.info("📝 Admin updating product: {}", productId);
        return ApiResponse.<ProductResponse>builder()
                .result(productService.updateProduct(productId, request))
                .build();
    }

    /**
     * POST /manage/product/add
     * Thêm product mới với đầy đủ thông tin bao gồm:
     * - Thông tin cơ bản: name, description, status, category
     * - Ảnh đại diện: image (MultipartFile)
     * - Ảnh giới thiệu: introImages (List<ProductImageRequest>)
     * - Variants với: color, detailImages, attributes
     * 
     * Tất cả ảnh sẽ được tự động lưu vào src/main/resources/static/images
     * với tên file tự động (UUID) và URL sẽ được lưu vào database
     * 
     * Yêu cầu: ROLE_ADMIN (được bảo vệ bởi SecurityConfig)
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<ProductResponse> addNewProduct(@ModelAttribute @Valid ProductRequest request) throws IOException {
        log.info("📦 Adding new product: {}", request.getName());
        log.info("   Category: {}", request.getCategory() != null ? request.getCategory().getId() : "null");
        log.info("   Has main image: {}", request.getImage() != null);
        log.info("   Intro images count: {}", request.getIntroImages() != null ? request.getIntroImages().size() : 0);
        log.info("   Variants count: {}", request.getVariants() != null ? request.getVariants().size() : 0);
        
        ProductResponse result = productService.addNewProduct(request);
        
        log.info("✅ Product created successfully: {}", result.getId());
        return ApiResponse.<ProductResponse>builder()
                .result(result)
                .build();
    }

    /**
     * DELETE /manage/product/delete/{productId}
     * Xóa product
     * Yêu cầu: ROLE_ADMIN
     */
    @DeleteMapping("/delete/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<Void> deleteProduct(@PathVariable String productId){
        log.info("🗑️ Admin deleting product: {}", productId);
        productService.delete(productId);
        return ApiResponse.<Void>builder().build();
    }

    /**
     * PUT /manage/product/inactive/{productId}
     * Thay đổi trạng thái product (active/inactive)
     * Yêu cầu: ROLE_ADMIN
     */
    @PutMapping("/inactive/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<Void> inactiveProduct(@PathVariable String productId){
        log.info("🔄 Admin toggling product status: {}", productId);
        productService.inactive(productId);
        return ApiResponse.<Void>builder().build();
    }
}