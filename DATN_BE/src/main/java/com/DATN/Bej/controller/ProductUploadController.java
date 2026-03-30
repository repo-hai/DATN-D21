package com.DATN.Bej.controller;

import com.DATN.Bej.dto.request.productRequest.CategoryRequest;
import com.DATN.Bej.dto.request.productRequest.ProductAttributeRequest;
import com.DATN.Bej.dto.request.productRequest.ProductImageRequest;
import com.DATN.Bej.dto.request.productRequest.ProductRequest;
import com.DATN.Bej.dto.request.productRequest.ProductVariantRequest;
import com.DATN.Bej.dto.response.productResponse.ProductResponse;
import com.DATN.Bej.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller đơn giản để upload product qua HTML form
 * Không yêu cầu authentication (public access)
 * 
 * ⚠️ LƯU Ý: Endpoint này không có bảo mật, chỉ dùng cho mục đích test/development
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/upload")
public class ProductUploadController {

    ProductService productService;

    /**
     * GET /upload/product
     * Hiển thị form HTML đơn giản để upload product
     * Không cần authentication
     */
    @GetMapping("/product")
    public String showUploadForm(Model model) {
        log.info("📝 Showing product upload form");
        // Trả về view name - Spring sẽ tìm file templates/product-upload.html
        return "product-upload";
    }

    /**
     * POST /upload/product
     * Nhận dữ liệu từ form và tạo product
     * Không cần authentication
     * 
     * ⚠️ LƯU Ý: Trong production, nên thêm authentication hoặc API key
     */
    @PostMapping("/product")
    public String uploadProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "status", defaultValue = "1") int status,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "introImages", required = false) MultipartFile[] introImages,
            HttpServletRequest request,
            Model model) throws IOException {
        
        log.info("📦 Uploading product - Name: {}, Category: {}", name, categoryId);
        
        try {
            // Tạo ProductRequest từ form data
            ProductRequest productRequest = new ProductRequest();
            productRequest.setName(name);
            productRequest.setDescription(description);
            productRequest.setStatus(status);
            productRequest.setImage(image);
            
            // Set category
            CategoryRequest categoryRequest = new CategoryRequest();
            categoryRequest.setId(categoryId);
            productRequest.setCategory(categoryRequest);
            
            // Xử lý intro images
            if (introImages != null && introImages.length > 0) {
                List<ProductImageRequest> introImageRequests = new ArrayList<>();
                for (int i = 0; i < introImages.length; i++) {
                    if (introImages[i] != null && !introImages[i].isEmpty()) {
                        ProductImageRequest imgRequest = new ProductImageRequest();
                        imgRequest.setFile(introImages[i]);
                        imgRequest.setSortIndex(i);
                        introImageRequests.add(imgRequest);
                    }
                }
                productRequest.setIntroImages(introImageRequests);
                log.info("📸 Added {} intro images", introImageRequests.size());
            }
            
            // Xử lý variants từ request parameters
            List<ProductVariantRequest> variants = parseVariantsFromRequest(request, introImages);
            if (variants != null && !variants.isEmpty()) {
                productRequest.setVariants(variants);
                log.info("🎨 Added {} variants", variants.size());
            }
            
            // Gọi service để tạo product
            ProductResponse result = productService.addNewProduct(productRequest);
            
            model.addAttribute("success", true);
            model.addAttribute("message", "Product uploaded successfully!");
            model.addAttribute("productId", result.getId());
            model.addAttribute("productName", result.getName());
            
            log.info("✅ Product uploaded successfully - ID: {}", result.getId());
            
        } catch (com.DATN.Bej.exception.AppException e) {
            // Xử lý AppException (có ErrorCode)
            log.error("❌ AppException: {} - {}", e.getErrorCode().getCode(), e.getMessage());
            model.addAttribute("success", false);
            model.addAttribute("message", "Error [" + e.getErrorCode().getCode() + "]: " + e.getErrorCode().getMessage());
        } catch (Exception e) {
            // Xử lý các exception khác
            log.error("❌ Failed to upload product: {}", e.getMessage(), e);
            log.error("   Exception type: {}", e.getClass().getName());
            if (e.getCause() != null) {
                log.error("   Caused by: {}", e.getCause().getMessage());
            }
            // Print stack trace để debug
            e.printStackTrace();
            
            model.addAttribute("success", false);
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            model.addAttribute("message", "Error: " + errorMessage);
        }
        
        return "product-upload";
    }
    
    /**
     * Parse variants từ HttpServletRequest
     * Form gửi dữ liệu dạng: variants[0].color, variants[0].attributes[0].name, etc.
     */
    private List<ProductVariantRequest> parseVariantsFromRequest(HttpServletRequest request, MultipartFile[] introImages) {
        List<ProductVariantRequest> variants = new ArrayList<>();
        
        // Tìm tất cả các variant indices
        Map<String, String[]> parameterMap = request.getParameterMap();
        int maxVariantIndex = -1;
        
        for (String paramName : parameterMap.keySet()) {
            if (paramName.startsWith("variants[") && paramName.contains("].color")) {
                try {
                    String indexStr = paramName.substring(paramName.indexOf("[") + 1, paramName.indexOf("]"));
                    int index = Integer.parseInt(indexStr);
                    if (index > maxVariantIndex) {
                        maxVariantIndex = index;
                    }
                } catch (NumberFormatException e) {
                    log.warn("⚠️ Invalid variant index in parameter: {}", paramName);
                }
            }
        }
        
        // Parse từng variant
        for (int i = 0; i <= maxVariantIndex; i++) {
            String color = request.getParameter("variants[" + i + "].color");
            if (color == null || color.trim().isEmpty()) {
                continue; // Skip variant nếu không có color
            }
            
            ProductVariantRequest variant = new ProductVariantRequest();
            variant.setColor(color);
            
            // Xử lý detail images cho variant
            // Lưu ý: Spring không tự động bind nested arrays, cần xử lý thủ công
            // Tạm thời để trống, có thể thêm sau nếu cần
            // TODO: Implement detail images parsing if needed
            variant.setDetailImages(new ArrayList<>());
            
            // Xử lý attributes cho variant
            List<ProductAttributeRequest> attributes = parseAttributesFromRequest(request, i);
            if (attributes != null && !attributes.isEmpty()) {
                variant.setAttributes(attributes);
            }
            
            variants.add(variant);
        }
        
        return variants;
    }
    
    /**
     * Parse attributes cho một variant cụ thể
     */
    private List<ProductAttributeRequest> parseAttributesFromRequest(HttpServletRequest request, int variantIndex) {
        List<ProductAttributeRequest> attributes = new ArrayList<>();
        
        // Tìm tất cả các attribute indices cho variant này
        Map<String, String[]> parameterMap = request.getParameterMap();
        int maxAttrIndex = -1;
        
        for (String paramName : parameterMap.keySet()) {
            String prefix = "variants[" + variantIndex + "].attributes[";
            if (paramName.startsWith(prefix) && paramName.contains("].name")) {
                try {
                    String indexStr = paramName.substring(prefix.length(), paramName.indexOf("]"));
                    int index = Integer.parseInt(indexStr);
                    if (index > maxAttrIndex) {
                        maxAttrIndex = index;
                    }
                } catch (NumberFormatException e) {
                    log.warn("⚠️ Invalid attribute index in parameter: {}", paramName);
                }
            }
        }
        
        // Parse từng attribute
        for (int i = 0; i <= maxAttrIndex; i++) {
            String name = request.getParameter("variants[" + variantIndex + "].attributes[" + i + "].name");
            if (name == null || name.trim().isEmpty()) {
                continue; // Skip attribute nếu không có name
            }
            
            ProductAttributeRequest attribute = new ProductAttributeRequest();
            attribute.setName(name);
            
            try {
                String originalPriceStr = request.getParameter("variants[" + variantIndex + "].attributes[" + i + "].originalPrice");
                if (originalPriceStr != null && !originalPriceStr.isEmpty()) {
                    attribute.setOriginalPrice(Integer.parseInt(originalPriceStr));
                }
                
                String finalPriceStr = request.getParameter("variants[" + variantIndex + "].attributes[" + i + "].finalPrice");
                if (finalPriceStr != null && !finalPriceStr.isEmpty()) {
                    attribute.setFinalPrice(Integer.parseInt(finalPriceStr));
                }
                
                String discountStr = request.getParameter("variants[" + variantIndex + "].attributes[" + i + "].discount");
                if (discountStr != null && !discountStr.isEmpty()) {
                    attribute.setDiscount(Integer.parseInt(discountStr));
                }
                
                String stockStr = request.getParameter("variants[" + variantIndex + "].attributes[" + i + "].stockQuantity");
                if (stockStr != null && !stockStr.isEmpty()) {
                    attribute.setStockQuantity(Integer.parseInt(stockStr));
                }
                
                String soldStr = request.getParameter("variants[" + variantIndex + "].attributes[" + i + "].soldQuantity");
                if (soldStr != null && !soldStr.isEmpty()) {
                    attribute.setSoldQuantity(Integer.parseInt(soldStr));
                } else {
                    attribute.setSoldQuantity(0);
                }
            } catch (NumberFormatException e) {
                log.warn("⚠️ Invalid number format for attribute {} in variant {}: {}", i, variantIndex, e.getMessage());
                continue; // Skip attribute nếu có lỗi parse số
            }
            
            attributes.add(attribute);
        }
        
        return attributes;
    }
    
}

