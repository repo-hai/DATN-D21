package com.DATN.Bej.service;

import com.DATN.Bej.dto.request.productRequest.ProductAttributeRequest;
import com.DATN.Bej.dto.request.productRequest.ProductImageRequest;
import com.DATN.Bej.dto.request.productRequest.ProductRequest;
import com.DATN.Bej.dto.request.productRequest.ProductVariantRequest;
import com.DATN.Bej.dto.response.PageResponse;
import com.DATN.Bej.dto.response.productResponse.ProductListResponse;
import com.DATN.Bej.dto.response.productResponse.ProductResponse;
import com.DATN.Bej.entity.product.*;
import com.DATN.Bej.exception.AppException;
import com.DATN.Bej.exception.ErrorCode;
import com.DATN.Bej.mapper.product.CategoryMapper;
import com.DATN.Bej.mapper.product.ProductAttributeMapper;
import com.DATN.Bej.mapper.product.ProductMapper;
import com.DATN.Bej.mapper.product.ProductVariantMapper;
import com.DATN.Bej.repository.product.CategoryRepository;
import com.DATN.Bej.repository.product.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ProductService {

    ProductRepository productRepository;
    CategoryRepository categoryRepository;

    ProductMapper productMapper;
    CategoryMapper categoryMapper;
    ProductVariantMapper productVariantMapper;
    ProductAttributeMapper productAttributeMapper;
    FileStorageService fileStorageService;
    CloudinaryService cloudinaryService;

//    private final UserRepository userRepository;

    //    @PreAuthorize((has))
    public List<ProductListResponse> getProducts(){
        return productRepository.findByStatusOrderByCreateDateDesc(1).stream().map(productMapper::toProductListResponse).toList();
    }
    
    /**
     * Lấy danh sách products có phân trang
     * @param page Số trang (0-based, mặc định 0)
     * @param size Số items mỗi trang (mặc định 20)
     * @return PageResponse chứa danh sách products và thông tin phân trang
     */
    public PageResponse<ProductListResponse> getProductsPaginated(int page, int size) {
        log.info("📋 Getting products with pagination - Page: {}, Size: {}", page, size);
        
        // Validate và set default values
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
        if (size > 100) size = 100; // Giới hạn tối đa 100 items/trang
        
        // Tạo Pageable với sort theo createDate DESC
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate"));
        
        // Lấy page từ repository
        Page<Product> productPage = productRepository.findByStatusOrderByCreateDateDesc(1, pageable);
        
        // Map sang DTO
        List<ProductListResponse> content = productPage.getContent().stream()
                .map(productMapper::toProductListResponse)
                .toList();
        
        // Tạo PageResponse
        PageResponse<ProductListResponse> response = PageResponse.<ProductListResponse>builder()
                .content(content)
                .page(productPage.getNumber())
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .first(productPage.isFirst())
                .last(productPage.isLast())
                .hasNext(productPage.hasNext())
                .hasPrevious(productPage.hasPrevious())
                .build();
        
        log.info("✅ Retrieved {} products (page {}/{}, total: {})", 
                content.size(), page + 1, productPage.getTotalPages(), productPage.getTotalElements());
        
        return response;
    }
    
    /**
     * Tìm kiếm products có phân trang
     * @param categoryId ID category (optional)
     * @param name Tên product (optional)
     * @param page Số trang (0-based)
     * @param size Số items mỗi trang
     * @return PageResponse chứa danh sách products và thông tin phân trang
     */
    public PageResponse<ProductListResponse> searchProductsPaginated(Long categoryId, String name, int page, int size) {
        log.info("🔍 Searching products with pagination - Category: {}, Name: {}, Page: {}, Size: {}", 
                categoryId, name, page, size);
        
        // Validate và set default values
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
        if (size > 100) size = 100;
        
        // Tạo Pageable với sort theo createDate DESC
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate"));
        
        Page<Product> productPage;
        
        if (categoryId != null && name != null && !name.trim().isEmpty()) {
            // Tìm theo cả category và name
            productPage = productRepository.findByCategoryAndNameContainingIgnoreCaseAndStatus(
                    categoryId, name, 1, pageable);
        } else if (categoryId != null) {
            // Chỉ tìm theo category
            productPage = productRepository.findByCategory_IdAndStatusOrderByCreateDateDesc(categoryId, 1, pageable);
        } else if (name != null && !name.trim().isEmpty()) {
            // Chỉ tìm theo name
            productPage = productRepository.findByNameContainingIgnoreCaseAndStatus(name, 1, pageable);
        } else {
            // Không có điều kiện nào, trả về tất cả
            productPage = productRepository.findByStatusOrderByCreateDateDesc(1, pageable);
        }
        
        // Map sang DTO
        List<ProductListResponse> content = productPage.getContent().stream()
                .map(productMapper::toProductListResponse)
                .toList();
        
        // Tạo PageResponse
        PageResponse<ProductListResponse> response = PageResponse.<ProductListResponse>builder()
                .content(content)
                .page(productPage.getNumber())
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .first(productPage.isFirst())
                .last(productPage.isLast())
                .hasNext(productPage.hasNext())
                .hasPrevious(productPage.hasPrevious())
                .build();
        
        log.info("✅ Found {} products (page {}/{}, total: {})", 
                content.size(), page + 1, productPage.getTotalPages(), productPage.getTotalElements());
        
        return response;
    }
    
    /**
     * Tìm products theo category ID
     * @param categoryId ID của category
     * @return Danh sách products thuộc category đó (chỉ lấy status = 1)
     */
    public List<ProductListResponse> getProductsByCategory(Long categoryId) {
        log.info("📋 Getting products by category: {}", categoryId);
        List<ProductListResponse> products = productRepository
            .findByCategory_IdAndStatusOrderByCreateDateDesc(categoryId, 1)
            .stream()
            .map(productMapper::toProductListResponse)
            .toList();
        log.info("✅ Found {} products in category {}", products.size(), categoryId);
        return products;
    }
    
    /**
     * Tìm products theo tên (gần đúng)
     * @param name Tên product cần tìm (có thể là một phần của tên)
     * @return Danh sách products có tên chứa keyword (chỉ lấy status = 1)
     */
    public List<ProductListResponse> searchProductsByName(String name) {
        log.info("📋 Searching products by name: {}", name);
        List<ProductListResponse> products = productRepository
            .findByNameContainingIgnoreCaseAndStatus(name, 1)
            .stream()
            .map(productMapper::toProductListResponse)
            .toList();
        log.info("✅ Found {} products matching name: {}", products.size(), name);
        return products;
    }
    
    /**
     * Tìm products theo category và tên (gần đúng)
     * @param categoryId ID của category (có thể null để tìm tất cả categories)
     * @param name Tên product cần tìm (có thể null để tìm tất cả)
     * @return Danh sách products thỏa mãn cả 2 điều kiện (chỉ lấy status = 1)
     */
    public List<ProductListResponse> searchProducts(Long categoryId, String name) {
        log.info("📋 Searching products - Category: {}, Name: {}", categoryId, name);
        
        List<Product> products;
        
        if (categoryId != null && name != null && !name.trim().isEmpty()) {
            // Tìm theo cả category và name
            products = productRepository.findByCategoryAndNameContainingIgnoreCaseAndStatus(categoryId, name, 1);
        } else if (categoryId != null) {
            // Chỉ tìm theo category
            products = productRepository.findByCategory_IdAndStatusOrderByCreateDateDesc(categoryId, 1);
        } else if (name != null && !name.trim().isEmpty()) {
            // Chỉ tìm theo name
            products = productRepository.findByNameContainingIgnoreCaseAndStatus(name, 1);
        } else {
            // Không có điều kiện nào, trả về tất cả
            products = productRepository.findByStatusOrderByCreateDateDesc(1);
        }
        
        List<ProductListResponse> result = products.stream()
            .map(productMapper::toProductListResponse)
            .toList();
        
        log.info("✅ Found {} products", result.size());
        return result;
    }


    //    admin service
    // admin get
//    @PreAuthorize("hasRole('ADMIN')")
    public List<ProductListResponse> getAllProducts(){
        return productRepository.findAllByOrderByCreateDateDesc().stream().map(productMapper::toProductListResponse).toList();
    }
    // get 1
    public ProductResponse getProductDetails(String productId){
//        System.out.println(productId);
        return productMapper.toProductResponse(productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    // add new
    public ProductResponse addNewProduct(ProductRequest request) throws IOException {
        System.out.println("product add");
        if(productRepository.existsByName(request.getName())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        Product product = productMapper.toProduct(request);

        Category category = categoryRepository.findById(request.getCategory().getId()).orElseThrow(
                () -> new AppException(ErrorCode.ROLE_NOT_FOUND)
        );
        System.out.println("category: " + category.getId());
        product.setCategory(category);
        product.setCreateDate(LocalDate.now());
        System.out.println(product.getName());

        if (request.getImage() != null) {
            String image = saveFile(request.getImage());
            product.setImage(image);
        }
        if(request.getIntroImages() != null){
            product.setIntroImages(mpIntroImages(request.getIntroImages(), product));
        }
        if (request.getVariants() != null){
            List<ProductVariant> variants = mpVariants(request.getVariants(), product);
            product.setVariants(variants);
        }
        System.out.println("=== PRODUCT UPDATE DEBUG ===");
        System.out.println("Product ID: " + product.getId());
        System.out.println("IntroImages: ");
        if (product.getIntroImages() != null) {
            product.getIntroImages().forEach(img ->
                    System.out.println(" - id=" + img.getId() + ", url=" + img.getUrl()));
        } else {
            System.out.println(" - (null)");
        }

        System.out.println("Variants: ");
        if (product.getVariants() != null) {
            product.getVariants().forEach(variant -> {
                System.out.println(" - Variant id=" + variant.getId() + ", color=" + variant.getColor());

                System.out.println("   DetailImages:");
                if (variant.getDetailImages() != null) {
                    variant.getDetailImages().forEach(img ->
                            System.out.println("     * id=" + img.getId() + ", url=" + img.getUrl()));
                } else {
                    System.out.println("     * (null)");
                }

                System.out.println("   Attributes:");
                if (variant.getAttributes() != null) {
                    variant.getAttributes().forEach(attr ->
                            System.out.println("     * id=" + attr.getId() + ", name=" + attr.getName()));
                } else {
                    System.out.println("     * (null)");
                }
            });
        } else {
            System.out.println(" - (null)");
        }
        System.out.println("update");


        return productMapper.toProductResponse(productRepository.save(product));
    }
// add new ----------------------------------------------------------------------------------------

    // update new ----------------------------------------------------------------------------------------
    @Transactional
    public ProductResponse updateProduct(String productId, ProductRequest request) throws IOException {
        Product product = productRepository.findById(productId).orElseThrow(
                ()  -> new AppException(ErrorCode.USER_NOT_EXISTED));

        productMapper.updateProduct(product, request);
        Category category = categoryRepository.findById(request.getCategory().getId()).orElseThrow(
                () -> new AppException(ErrorCode.ROLE_NOT_FOUND)
        );
        System.out.println("category: " + category.getId());
        product.setCategory(category);
        System.out.println(product.getCategory().getName());
        // cập nhật ảnh đại diện
        if (request.getImage() != null) {
            String image = saveFile(request.getImage());
            product.setImage(image);
        }

        // intro images
        if (request.getIntroImages() != null) {
            Map<String, ProductImage> oldImages = product.getIntroImages().stream()
                    .filter(img -> img.getId() != null)
                    .collect(Collectors.toMap(ProductImage::getId, Function.identity()));

            List<ProductImage> updatedImages = new ArrayList<>();
            for (ProductImageRequest reqImg : request.getIntroImages()) {
                if (reqImg.getId() != null && oldImages.containsKey(reqImg.getId())) {
                    ProductImage img = oldImages.get(reqImg.getId());
                    if (reqImg.getFile() != null) {
                        img.setUrl(saveFile(reqImg.getFile()));
                    }
                    updatedImages.add(img);
                } else {
                    ProductImage newImg = mpImage(reqImg.getFile());
                    newImg.setProduct(product);
                    updatedImages.add(newImg);
                }
            }
            // dùng clear + addAll thay cho setIntroImages
            product.getIntroImages().clear();
            product.getIntroImages().addAll(updatedImages);
        }

        // variants
        if (request.getVariants() != null) {
            Map<String, ProductVariant> oldVariants = product.getVariants().stream()
                    .filter(v -> v.getId() != null)
                    .collect(Collectors.toMap(ProductVariant::getId, Function.identity()));

            List<ProductVariant> updatedVariants = new ArrayList<>();
            for (ProductVariantRequest reqVar : request.getVariants()) {
                if (reqVar.getId() != null && oldVariants.containsKey(reqVar.getId())) {
                    ProductVariant variant = oldVariants.get(reqVar.getId());
                    variant.setColor(reqVar.getColor());

                    // detail images
                    if (reqVar.getDetailImages() != null) {
                        Map<String, ProductImage> oldDetailImgs = variant.getDetailImages().stream()
                                .filter(img -> img.getId() != null)
                                .collect(Collectors.toMap(ProductImage::getId, Function.identity()));

                        List<ProductImage> newDetailImgs = new ArrayList<>();
                        for (ProductImageRequest imgReq : reqVar.getDetailImages()) {
                            if (imgReq.getId() != null && oldDetailImgs.containsKey(imgReq.getId())) {
                                ProductImage img = oldDetailImgs.get(imgReq.getId());
                                if (imgReq.getFile() != null) {
                                    img.setUrl(saveFile(imgReq.getFile()));
                                }
                                newDetailImgs.add(img);
                            } else {
                                ProductImage img = mpImage(imgReq.getFile());
                                img.setVariant(variant);
                                newDetailImgs.add(img);
                            }
                        }
                        variant.getDetailImages().clear();
                        variant.getDetailImages().addAll(newDetailImgs);
                    }

                    // attributes
                    if (reqVar.getAttributes() != null) {
                        Map<String, ProductAttribute> oldAttrs = variant.getAttributes().stream()
                                .filter(a -> a.getId() != null)
                                .collect(Collectors.toMap(ProductAttribute::getId, Function.identity()));

                        List<ProductAttribute> newAttrs = new ArrayList<>();
                        for (ProductAttributeRequest attrReq : reqVar.getAttributes()) {
                            if (attrReq.getId() != null && oldAttrs.containsKey(attrReq.getId())) {
                                ProductAttribute attr = oldAttrs.get(attrReq.getId());
                                attr.setName(attrReq.getName());
                                attr.setOriginalPrice(attrReq.getOriginalPrice());
                                attr.setFinalPrice(attrReq.getFinalPrice());
                                newAttrs.add(attr);
                            } else {
                                ProductAttribute attr = productAttributeMapper.toProductAttribute(attrReq);
                                attr.setVariant(variant);
                                newAttrs.add(attr);
                            }
                        }
                        variant.getAttributes().clear();
                        variant.getAttributes().addAll(newAttrs);
                    }
                    updatedVariants.add(variant);
                } else {
                    // thêm variant mới
                    ProductVariant newVariant = productVariantMapper.toVariant(reqVar);
                    newVariant.setProduct(product);

                    if (reqVar.getDetailImages() != null) {
                        newVariant.setDetailImages(mpDetailImages(reqVar.getDetailImages(), newVariant));
                    }
                    if (reqVar.getAttributes() != null) {
                        newVariant.setAttributes(mpAttributes(reqVar.getAttributes(), newVariant));
                    }
                    updatedVariants.add(newVariant);
                }
            }

            product.getVariants().clear();
            product.getVariants().addAll(updatedVariants);
        }

        System.out.println("update");

        System.out.println("=== PRODUCT UPDATE DEBUG ===");
        System.out.println("Product ID: " + product.getId());
        System.out.println("IntroImages: ");
        if (product.getIntroImages() != null) {
            product.getIntroImages().forEach(img ->
                    System.out.println(" - id=" + img.getId() + ", url=" + img.getUrl()));
        } else {
            System.out.println(" - (null)");
        }

        System.out.println("Variants: ");
        if (product.getVariants() != null) {
            product.getVariants().forEach(variant -> {
                System.out.println(" - Variant id=" + variant.getId() + ", color=" + variant.getColor());

                System.out.println("   DetailImages:");
                if (variant.getDetailImages() != null) {
                    variant.getDetailImages().forEach(img ->
                            System.out.println("     * id=" + img.getId() + ", url=" + img.getUrl()));
                } else {
                    System.out.println("     * (null)");
                }

                System.out.println("   Attributes:");
                if (variant.getAttributes() != null) {
                    variant.getAttributes().forEach(attr ->
                            System.out.println("     * id=" + attr.getId() + ", name=" + attr.getName()));
                } else {
                    System.out.println("     * (null)");
                }
            });
        } else {
            System.out.println(" - (null)");
        }

        return productMapper.toProductResponse(productRepository.save(product));
    }

    // update new ----------------------------------------------------------------------------------------
    //delete
    public void delete(String productId){
        productRepository.deleteById(productId);
    }
    //set status
    public void inactive(String productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        int status = product.getStatus();
        if(status == 1) product.setStatus(0);
        else product.setStatus(1);
        productRepository.save(product);
    }

    //    mapping
    // variants
    private List<ProductVariant> mpVariants(List<ProductVariantRequest> variantRequests, Product product){
        return variantRequests.stream()
                .map(req -> {
                    ProductVariant variant = productVariantMapper.toVariant(req);

                    variant.setProduct(product);
                    variant.setColor(req.getColor());
//                    variant.setOriginalPrice(req.getOriginalPrice());
//                    variant.setFinalPrice(req.getFinalPrice());

                    if(req.getDetailImages() != null){
                        System.out.println("list images!");
                        variant.setDetailImages(mpDetailImages(req.getDetailImages(), variant));
                    }
                    if(req.getAttributes() != null){
                        variant.setAttributes(mpAttributes(req.getAttributes(), variant));
                    }
                    return variant;
                }).toList();
    }
    // images
    private ProductImage mpImage(MultipartFile file){
        ProductImage img = new ProductImage();
        try {
            img.setUrl(saveFile(file));
        } catch (IOException e) {
            throw new RuntimeException("Lỗi lưu ảnh!", e);
        }
        return img;
    }
    private List<ProductImage> mpDetailImages(List<ProductImageRequest> files, ProductVariant variant){
        return files.stream()
                .map(file -> {
                    ProductImage img = mpImage(file.getFile());
                    img.setVariant(variant);
                    return img;
                }).toList();
    }
    private List<ProductImage> mpIntroImages(List<ProductImageRequest> files, Product product){
        return files.stream()
                .map(file -> {
                    ProductImage img = mpImage(file.getFile());
                    img.setProduct(product);
                    return img;
                }).toList();
    }
    // attributes
    private List<ProductAttribute> mpAttributes(List<ProductAttributeRequest> attributesReq, ProductVariant variant){
        return attributesReq.stream()
                .map(req -> {
                    ProductAttribute attribute = productAttributeMapper.toProductAttribute(req);
                    attribute.setVariant(variant);
                    attribute.setName(req.getName());
                    attribute.setOriginalPrice(req.getOriginalPrice());
                    attribute.setFinalPrice(req.getFinalPrice());
                    return attribute;
                }).toList();
    }

//    save file
//    String saveFile(MultipartFile file) throws IOException {
//        String uploadDir = "D:/Spring/newVuePr/pimg/";
//        Path path = Paths.get(uploadDir + file.getOriginalFilename());
//        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//        return uploadDir + file.getOriginalFilename();
//    }

    /**
     * Lưu file ảnh và trả về URL.
     * Hiện tại flow sẽ:
     * 1. Upload ảnh lên Cloudinary
     * 2. Lấy secure_url trả về và lưu xuống DB
     */
    private String saveFile(MultipartFile file) throws IOException {
        // Upload lên Cloudinary và lấy URL
        return cloudinaryService.uploadImage(file);
    }
}
