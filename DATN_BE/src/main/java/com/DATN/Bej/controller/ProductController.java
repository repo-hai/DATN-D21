package com.DATN.Bej.controller;


import com.DATN.Bej.dto.request.ApiResponse;
import com.DATN.Bej.dto.response.HomeResponse;
import com.DATN.Bej.dto.response.PageResponse;
import com.DATN.Bej.dto.response.guest.ProductDetailRes;
import com.DATN.Bej.dto.response.productResponse.ProductListResponse;
import com.DATN.Bej.service.BannerService;
import com.DATN.Bej.service.ProductService;
import com.DATN.Bej.service.guest.GuestProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/home")
public class ProductController {

    ProductService productService;
    GuestProductService guestProductService;
    BannerService bannerService;

    /**
     * GET /home
     * Lấy dữ liệu Home page bao gồm banners và products
     */
    @GetMapping
    ApiResponse<HomeResponse> getHomeData() {
        log.info("📋 Getting home data (banners + products)");
        HomeResponse homeResponse = HomeResponse.builder()
                .banners(bannerService.getActiveBanners())
                .products(productService.getProducts())
                .build();
        log.info("✅ Home data retrieved: {} banners, {} products", 
                homeResponse.getBanners().size(), 
                homeResponse.getProducts().size());
        return ApiResponse.<HomeResponse>builder()
                .result(homeResponse)
                .build();
    }

    /**
     * GET /home/products
     * Lấy danh sách products (giữ lại để backward compatibility)
     */
    @GetMapping("/products")
    ApiResponse<List<ProductListResponse>> getProducts() {
        log.info("📋 Getting products list");
        return ApiResponse.<List<ProductListResponse>>builder()
                .result(productService.getProducts())
                .build();
    }

    /**
     * GET /home/product/{productId}
     * Lấy chi tiết product (không cần authentication)
     * 
     * @param productId ID của product
     * @return Chi tiết product bao gồm: thông tin cơ bản, intro images, variants, attributes, detail images
     * 
     * Example:
     * - GET /home/product/abc123 → Lấy chi tiết product có ID = abc123
     */
    @GetMapping("/product/{productId}")
    ApiResponse<ProductDetailRes> getProductDetails(@PathVariable String productId){
        log.info("📋 Getting product details for: {}", productId);
        ProductDetailRes result = guestProductService.getProductDetails(productId);
        log.info("✅ Product details retrieved: {}", productId);
        return ApiResponse.<ProductDetailRes>builder()
                .result(result)
                .build();
    }
    
    /**
     * GET /home/products/search?categoryId={categoryId}&name={name}
     * Tìm kiếm products theo category và/hoặc tên
     * 
     * @param categoryId (optional) ID của category để filter
     * @param name (optional) Tên product để tìm kiếm (gần đúng, không phân biệt hoa thường)
     * @return Danh sách products thỏa mãn điều kiện
     * 
     * Examples:
     * - GET /home/products/search?categoryId=10 → Tìm tất cả products trong category 10
     * - GET /home/products/search?name=iPhone → Tìm tất cả products có tên chứa "iPhone"
     * - GET /home/products/search?categoryId=10&name=iPhone → Tìm products trong category 10 và có tên chứa "iPhone"
     */
    @GetMapping("/products/search")
    ApiResponse<List<ProductListResponse>> searchProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String name) {
        log.info("🔍 Searching products - Category: {}, Name: {}", categoryId, name);
        return ApiResponse.<List<ProductListResponse>>builder()
                .result(productService.searchProducts(categoryId, name))
                .build();
    }
    
    /**
     * GET /home/products/category/{categoryId}
     * Lấy danh sách products theo category
     * 
     * @param categoryId ID của category
     * @return Danh sách products thuộc category đó
     */
    @GetMapping("/products/category/{categoryId}")
    ApiResponse<List<ProductListResponse>> getProductsByCategory(@PathVariable Long categoryId) {
        log.info("📋 Getting products by category: {}", categoryId);
        return ApiResponse.<List<ProductListResponse>>builder()
                .result(productService.getProductsByCategory(categoryId))
                .build();
    }
    
    /**
     * GET /home/products/search/name?q={query}
     * Tìm kiếm products theo tên (gần đúng)
     * 
     * @param query Từ khóa tìm kiếm (có thể là một phần của tên product)
     * @return Danh sách products có tên chứa từ khóa
     */
    @GetMapping("/products/search/name")
    ApiResponse<List<ProductListResponse>> searchProductsByName(@RequestParam String q) {
        log.info("🔍 Searching products by name: {}", q);
        return ApiResponse.<List<ProductListResponse>>builder()
                .result(productService.searchProductsByName(q))
                .build();
    }
    
    /**
     * GET /home/products/page?page={page}&size={size}
     * Lấy danh sách products có phân trang
     * 
     * @param page Số trang (0-based, mặc định 0)
     * @param size Số items mỗi trang (mặc định 20, tối đa 100)
     * @return PageResponse chứa danh sách products và thông tin phân trang
     * 
     * Example:
     * - GET /home/products/page?page=0&size=20 → Trang đầu tiên, 20 items
     * - GET /home/products/page?page=1&size=10 → Trang thứ 2, 10 items
     */
    @GetMapping("/products/page")
    ApiResponse<PageResponse<ProductListResponse>> getProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("📋 Getting products with pagination - Page: {}, Size: {}", page, size);
        return ApiResponse.<PageResponse<ProductListResponse>>builder()
                .result(productService.getProductsPaginated(page, size))
                .build();
    }
    
    /**
     * GET /home/products/search/page?categoryId={categoryId}&name={name}&page={page}&size={size}
     * Tìm kiếm products có phân trang
     * 
     * @param categoryId (optional) ID của category để filter
     * @param name (optional) Tên product để tìm kiếm (gần đúng)
     * @param page Số trang (0-based, mặc định 0)
     * @param size Số items mỗi trang (mặc định 20, tối đa 100)
     * @return PageResponse chứa danh sách products và thông tin phân trang
     * 
     * Examples:
     * - GET /home/products/search/page?categoryId=10&page=0&size=20
     * - GET /home/products/search/page?name=iPhone&page=0&size=10
     * - GET /home/products/search/page?categoryId=10&name=iPhone&page=0&size=20
     */
    @GetMapping("/products/search/page")
    ApiResponse<PageResponse<ProductListResponse>> searchProductsPaginated(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("🔍 Searching products with pagination - Category: {}, Name: {}, Page: {}, Size: {}", 
                categoryId, name, page, size);
        return ApiResponse.<PageResponse<ProductListResponse>>builder()
                .result(productService.searchProductsPaginated(categoryId, name, page, size))
                .build();
    }

//    @PostMapping()
}
