package com.DATN.Bej;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import com.DATN.Bej.dto.request.productRequest.CategoryRequest;
import com.DATN.Bej.dto.request.productRequest.ProductAttributeRequest;
import com.DATN.Bej.dto.request.productRequest.ProductImageRequest;
import com.DATN.Bej.dto.request.productRequest.ProductRequest;
import com.DATN.Bej.dto.request.productRequest.ProductVariantRequest;
import com.DATN.Bej.dto.response.PageResponse;
import com.DATN.Bej.dto.response.productResponse.ProductListResponse;
import com.DATN.Bej.dto.response.productResponse.ProductResponse;
import com.DATN.Bej.entity.product.Category;
import com.DATN.Bej.entity.product.Product;
import com.DATN.Bej.entity.product.ProductAttribute;
import com.DATN.Bej.entity.product.ProductImage;
import com.DATN.Bej.entity.product.ProductVariant;
import com.DATN.Bej.exception.AppException;
import com.DATN.Bej.exception.ErrorCode;
import com.DATN.Bej.mapper.product.CategoryMapper;
import com.DATN.Bej.mapper.product.ProductAttributeMapper;
import com.DATN.Bej.mapper.product.ProductMapper;
import com.DATN.Bej.mapper.product.ProductVariantMapper;
import com.DATN.Bej.repository.product.CategoryRepository;
import com.DATN.Bej.repository.product.ProductRepository;
import com.DATN.Bej.service.CloudinaryService;
import com.DATN.Bej.service.FileStorageService;
import com.DATN.Bej.service.ProductService;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    // ── Mocks ──────────────────────────────────────────────────────────────
    @Mock ProductRepository productRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock ProductMapper productMapper;
    @Mock CategoryMapper categoryMapper;
    @Mock ProductVariantMapper productVariantMapper;
    @Mock ProductAttributeMapper productAttributeMapper;
    @Mock FileStorageService fileStorageService;
    @Mock CloudinaryService cloudinaryService;

    @InjectMocks
    ProductService productService;

    // ── Helpers ─────────────────────────────────────────────────────────────
    /** Tạo một Product entity giả */
    private Product fakeProduct(String id, String name) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setStatus(1);
        return p;
    }

    /** Tạo một ProductListResponse giả */
    private ProductListResponse fakeListResponse(String name) {
        ProductListResponse r = new ProductListResponse();
        r.setName(name);
        return r;
    }

    /** Tạo Page<Product> giả từ danh sách */
    private Page<Product> fakePage(List<Product> products, Pageable pageable) {
        return new PageImpl<>(products, pageable, products.size());
    }


    // ════════════════════════════════════════════════════════════════════════
    // 1. getProductsPaginated
    // ════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("getProductsPaginated()")
    class GetProductsPaginatedTest {

        @Test
        @DisplayName("TC_PS_GPP_01 – Trả về đúng PageResponse khi page/size hợp lệ")
        void shouldReturnCorrectPageResponse_whenValidPageAndSize() {
            // Arrange
            int page = 0, size = 10;
            Product p = fakeProduct("p1", "iPhone 15");
            ProductListResponse dto = fakeListResponse("iPhone 15");

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate"));
            Page<Product> productPage = fakePage(List.of(p), pageable);

            when(productRepository.findByStatusOrderByCreateDateDesc(eq(1), any(Pageable.class)))
                    .thenReturn(productPage);
            when(productMapper.toProductListResponse(p)).thenReturn(dto);

            // Act
            PageResponse<ProductListResponse> result = productService.getProductsPaginated(page, size);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("iPhone 15");
            assertThat(result.getPage()).isEqualTo(0);
            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("TC_PS_GPP_02 – page âm thì mặc định về 0")
        void shouldDefaultPageToZero_whenNegativePage() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createDate"));
            when(productRepository.findByStatusOrderByCreateDateDesc(eq(1), any(Pageable.class)))
                    .thenReturn(Page.empty());

            // Act
            productService.getProductsPaginated(-5, 20);

            // Assert: repository được gọi với page = 0
            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(productRepository).findByStatusOrderByCreateDateDesc(eq(1), captor.capture());
            assertThat(captor.getValue().getPageNumber()).isEqualTo(0);
        }

        @Test
        @DisplayName("TC_PS_GPP_03 – size > 100 thì bị giới hạn về 100")
        void shouldCapSizeAtHundred_whenSizeExceedsLimit() {
            when(productRepository.findByStatusOrderByCreateDateDesc(eq(1), any(Pageable.class)))
                    .thenReturn(Page.empty());

            productService.getProductsPaginated(0, 500);

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(productRepository).findByStatusOrderByCreateDateDesc(eq(1), captor.capture());
            assertThat(captor.getValue().getPageSize()).isEqualTo(100);
        }

        
        @Test
        @DisplayName("TC_PS_GPP_04 – size <= 0 thì mặc định về 20")
        void shouldDefaultSizeToTwenty_whenZeroOrNegativeSize() {
            when(productRepository.findByStatusOrderByCreateDateDesc(eq(1), any(Pageable.class)))
                    .thenReturn(Page.empty());

            productService.getProductsPaginated(0, 0);

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(productRepository).findByStatusOrderByCreateDateDesc(eq(1), captor.capture());
            assertThat(captor.getValue().getPageSize()).isEqualTo(20);
        }

        @Test
        @DisplayName("TC_PS_GPP_05 – Không có sản phẩm nào thì trả về content rỗng")
        void shouldReturnEmptyContent_whenNoProducts() {
            when(productRepository.findByStatusOrderByCreateDateDesc(eq(1), any(Pageable.class)))
                    .thenReturn(Page.empty());

            PageResponse<ProductListResponse> result = productService.getProductsPaginated(0, 10);

            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
        }
    }


        // ════════════════════════════════════════════════════════════════════════
        // 2. searchProducts– 4 nhánh điều kiện
        // ════════════════════════════════════════════════════════════════════════
        @Nested
        @DisplayName("searchProductsPaginated()")
        class SearchProductsPaginatedTest {

        @Test
        @DisplayName("TC_PS_SPP_01 – Có cả categoryId và name → gọi findByCategoryAndName")
        void shouldSearchByCategoryAndName_whenBothProvided() {

            // =========================
            // Arrange - Chuẩn bị dữ liệu test
            // =========================

            // Tạo product giả lập trong database
            Product product = fakeProduct("p1", "iPhone 15");

            // Tạo DTO response sau khi map từ Product
            ProductListResponse dto = fakeListResponse("iPhone 15");

            // Tạo thông tin phân trang:
            // page = 0 (trang đầu tiên)
            // size = 10 (tối đa 10 phần tử)
            Pageable pageable = PageRequest.of(0, 10);

            // Tạo dữ liệu Page<Product> giả lập trả về từ repository
            Page<Product> pageData = fakePage(List.of(product), pageable);

            // Mock repository:
            // Khi tìm kiếm theo:
            // - categoryId = 1
            // - name chứa "iPhone"
            // - status = 1
            // thì trả về pageData
            when(productRepository.findByCategoryAndNameContainingIgnoreCaseAndStatus(
                    eq(1L), eq("iPhone"), eq(1), any(Pageable.class)))
                    .thenReturn(pageData);

            // Mock mapper:
            // Khi convert Product -> ProductListResponse
            // thì trả về dto đã chuẩn bị
            when(productMapper.toProductListResponse(product))
                    .thenReturn(dto);

            // =========================
            // Act - Gọi hàm cần test
            // =========================

            PageResponse<ProductListResponse> result =
                    productService.searchProductsPaginated(1L, "iPhone", 0, 10);

            // =========================
            // Assert - Kiểm tra kết quả
            // =========================

            // Kiểm tra danh sách trả về có đúng 1 phần tử
            assertThat(result.getContent()).hasSize(1);

            // Kiểm tra tên sản phẩm trả về đúng
            assertThat(result.getContent().get(0).getName())
                    .isEqualTo("iPhone 15");

            // Verify repository:
            // Đảm bảo repository được gọi đúng method tìm theo category + name
            verify(productRepository).findByCategoryAndNameContainingIgnoreCaseAndStatus(
                    eq(1L), eq("iPhone"), eq(1), any(Pageable.class));

            // Đảm bảo không có lời gọi dư thừa nào khác tới repository
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        @DisplayName("TC_PS_SPP_02 – Chỉ có categoryId → gọi findByCategory")
        void shouldSearchByCategoryOnly_whenOnlyCategoryProvided() {

            // =========================
            // Arrange
            // =========================

            // Tạo product giả lập
            Product product = fakeProduct("p2", "Samsung S24");

            // DTO response sau khi mapping
            ProductListResponse dto = fakeListResponse("Samsung S24");

            // Tạo pageable
            Pageable pageable = PageRequest.of(0, 10);

            // Tạo dữ liệu page trả về
            Page<Product> pageData = fakePage(List.of(product), pageable);

            // Mock repository:
            // Khi tìm theo categoryId = 2
            // thì trả về pageData
            when(productRepository.findByCategory_IdAndStatusOrderByCreateDateDesc(
                    eq(2L), eq(1), any(Pageable.class)))
                    .thenReturn(pageData);

            // Mock mapper
            when(productMapper.toProductListResponse(product))
                    .thenReturn(dto);

            // =========================
            // Act
            // =========================

            PageResponse<ProductListResponse> result =
                    productService.searchProductsPaginated(2L, null, 0, 10);

            // =========================
            // Assert
            // =========================

            // Kiểm tra số lượng phần tử trả về
            assertThat(result.getContent()).hasSize(1);

            // Kiểm tra đúng tên sản phẩm
            assertThat(result.getContent().get(0).getName())
                    .isEqualTo("Samsung S24");

            // Verify repository gọi đúng method tìm theo category
            verify(productRepository).findByCategory_IdAndStatusOrderByCreateDateDesc(
                    eq(2L), eq(1), any(Pageable.class));
        }

        @Test
        @DisplayName("TC_PS_SPP_03 – Chỉ có name → gọi findByName")
        void shouldSearchByNameOnly_whenOnlyNameProvided() {

            // =========================
            // Arrange
            // =========================

            // Tạo dữ liệu product giả
            Product product = fakeProduct("p3", "Samsung Galaxy S24");

            // Tạo DTO response
            ProductListResponse dto = fakeListResponse("Samsung Galaxy S24");

            // Pageable cho phân trang
            Pageable pageable = PageRequest.of(0, 10);

            // Page dữ liệu giả lập
            Page<Product> pageData = fakePage(List.of(product), pageable);

            // Mock repository:
            // Khi tìm theo name chứa "Samsung"
            // thì trả về dữ liệu pageData
            when(productRepository.findByNameContainingIgnoreCaseAndStatus(
                    eq("Samsung"), eq(1), any(Pageable.class)))
                    .thenReturn(pageData);

            // Mock mapper
            when(productMapper.toProductListResponse(product))
                    .thenReturn(dto);

            // =========================
            // Act
            // =========================

            PageResponse<ProductListResponse> result =
                    productService.searchProductsPaginated(null, "Samsung", 0, 10);

            // =========================
            // Assert
            // =========================

            // Kiểm tra danh sách trả về có đúng 1 phần tử
            assertThat(result.getContent()).hasSize(1);

            // Kiểm tra đúng tên sản phẩm
            assertThat(result.getContent().get(0).getName())
                    .isEqualTo("Samsung Galaxy S24");

            // Verify repository gọi đúng method tìm theo name
            verify(productRepository).findByNameContainingIgnoreCaseAndStatus(
                    eq("Samsung"), eq(1), any(Pageable.class));
        }

        @Test
        @DisplayName("TC_PS_SPP_04 – Không có điều kiện nào → lấy tất cả")
        void shouldReturnAll_whenNoCriteriaProvided() {

            // =========================
            // Arrange
            // =========================

            // Tạo object Product giả lập dữ liệu trong database
            Product p = fakeProduct("p1", "iPhone 15");

            // Tạo DTO response giả lập sau khi mapper convert từ Product
            ProductListResponse dto = fakeListResponse("iPhone 15");

            // Tạo thông tin phân trang
            Pageable pageable = PageRequest.of(0, 10);

            // Tạo Page<Product> giả lập dữ liệu trả về từ repository
            Page<Product> pageData = fakePage(List.of(p), pageable);

            // Mock repository:
            // Khi gọi lấy toàn bộ sản phẩm active
            // thì trả về pageData
            when(productRepository.findByStatusOrderByCreateDateDesc(eq(1), any(Pageable.class)))
                    .thenReturn(pageData);

            // Mock mapper:
            // Convert Product -> DTO
            when(productMapper.toProductListResponse(p))
                    .thenReturn(dto);

            // =========================
            // Act
            // =========================

            // Gọi hàm search mà không truyền category và name
            PageResponse<ProductListResponse> result =
                    productService.searchProductsPaginated(null, null, 0, 10);

            // =========================
            // Assert
            // =========================

            // Kiểm tra số lượng phần tử trả về
            assertThat(result.getContent()).hasSize(1);

            // Kiểm tra đúng tên sản phẩm
            assertThat(result.getContent().get(0).getName())
                    .isEqualTo("iPhone 15");

            // Verify repository gọi đúng method lấy tất cả
            verify(productRepository)
                    .findByStatusOrderByCreateDateDesc(eq(1), any(Pageable.class));
        }

        @Test
        @DisplayName("TC_PS_SPP_05 – name là chuỗi khoảng trắng → coi như không có name")
        void shouldIgnoreBlankName_andSearchByCategoryOnly() {

            // =========================
            // Arrange
            // =========================

            // Tạo product giả lập
            Product product = fakeProduct("p5", "Xiaomi 14");

            // Tạo DTO response
            ProductListResponse dto = fakeListResponse("Xiaomi 14");

            // Pageable
            Pageable pageable = PageRequest.of(0, 10);

            // Dữ liệu page trả về
            Page<Product> pageData = fakePage(List.of(product), pageable);

            // Mock repository:
            // Vì name chỉ chứa khoảng trắng,
            // service sẽ bỏ qua name và chỉ tìm theo category
            when(productRepository.findByCategory_IdAndStatusOrderByCreateDateDesc(
                    eq(3L), eq(1), any(Pageable.class)))
                    .thenReturn(pageData);

            // Mock mapper
            when(productMapper.toProductListResponse(product))
                    .thenReturn(dto);

            // =========================
            // Act
            // =========================

            PageResponse<ProductListResponse> result =
                    productService.searchProductsPaginated(3L, "   ", 0, 10);

            // =========================
            // Assert
            // =========================

            // Kiểm tra có đúng 1 phần tử
            assertThat(result.getContent()).hasSize(1);

            // Kiểm tra đúng tên sản phẩm
            assertThat(result.getContent().get(0).getName())
                    .isEqualTo("Xiaomi 14");

            // Verify repository:
            // Đảm bảo service gọi method tìm theo category
            // thay vì tìm theo name
            verify(productRepository).findByCategory_IdAndStatusOrderByCreateDateDesc(
                    eq(3L), eq(1), any(Pageable.class));
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // 3. getProductDetails 
    // ════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("getProductDetails()")
    class GetProductDetailsTest {

        @Test
        @DisplayName("TC_PS_GPD_01 – Tìm thấy product → trả về ProductResponse")
        void shouldReturnProductResponse_whenProductExists() {

            // =========================
            // Arrange - Chuẩn bị dữ liệu test
            // =========================

            // Tạo object Product giả lập dữ liệu lấy từ database
            // id = "abc123"
            // name = "iPhone 15 Pro"
            Product p = fakeProduct("abc123", "iPhone 15 Pro");

            // Tạo object response giả lập sau khi mapper convert
            // từ Product -> ProductResponse
            ProductResponse expected = new ProductResponse();
            expected.setName("iPhone 15 Pro");

            // Mock repository:
            // Khi gọi findById("abc123")
            // thì trả về Optional chứa product p
            when(productRepository.findById("abc123"))
                    .thenReturn(Optional.of(p));

            // Mock mapper:
            // Khi mapper convert product p
            // thì trả về object expected
            when(productMapper.toProductResponse(p))
                    .thenReturn(expected);

            // =========================
            // Act - Gọi hàm cần test
            // =========================

            // Thực thi hàm lấy chi tiết sản phẩm
            ProductResponse result =
                    productService.getProductDetails("abc123");

            // =========================
            // Assert - Kiểm tra kết quả
            // =========================

            // Kiểm tra kết quả trả về không null
            assertThat(result).isNotNull();

            // Kiểm tra tên sản phẩm đúng như mong đợi
            assertThat(result.getName())
                    .isEqualTo("iPhone 15 Pro");
        }

        @Test
        @DisplayName("TC16 – Không tìm thấy product → throw AppException USER_NOT_EXISTED")
        void shouldThrowAppException_whenProductNotFound() {

            // =========================
            // Arrange
            // =========================

            // Mock repository:
            // Khi tìm product với id = "not-exist"
            // thì trả về Optional.empty()
            // nghĩa là không tồn tại product
            when(productRepository.findById("not-exist"))
                    .thenReturn(Optional.empty());

            // =========================
            // Act + Assert
            // =========================

            // Kiểm tra khi gọi service
            // thì sẽ throw AppException
            assertThatThrownBy(() ->
                    productService.getProductDetails("not-exist"))

                    // Kiểm tra đúng kiểu exception
                    .isInstanceOf(AppException.class)

                    // Kiểm tra error code bên trong exception
                    .satisfies(ex ->
                            assertThat(((AppException) ex).getErrorCode())
                                    .isEqualTo(ErrorCode.USER_NOT_EXISTED));
        }
    }


    // ════════════════════════════════════════════════════════════════════════
    // 5. addNewProduct
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("addNewProduct()")
    class AddNewProductTest {

        /**
         * Hàm hỗ trợ tạo ProductRequest dùng chung cho các test case
         *
         * @param name       tên sản phẩm
         * @param categoryId id category
         * @return ProductRequest hoàn chỉnh
         */
        private ProductRequest buildRequest(String name, Long categoryId) {

            // Tạo request chính
            ProductRequest req = new ProductRequest();

            // Gán tên sản phẩm
            req.setName(name);

            // Tạo category request
            CategoryRequest catReq = new CategoryRequest();

            // Gán category id
            catReq.setId(categoryId);

            // Set category vào request
            req.setCategory(catReq);

            return req;
        }

        @Test
        @DisplayName("TC_PS_ANP_01 – Tên đã tồn tại → throw AppException USER_EXISTED")
        void shouldThrowException_whenProductNameAlreadyExists() {

            // =========================
            // Arrange
            // =========================

            // Tạo request thêm mới product
            ProductRequest req =
                    buildRequest("iPhone 15", 1L);

            // Mock repository:
            // Khi kiểm tra tên "iPhone 15"
            // thì trả về true
            // nghĩa là tên đã tồn tại trong database
            when(productRepository.existsByName("iPhone 15"))
                    .thenReturn(true);

            // =========================
            // Act + Assert
            // =========================

            // Kiểm tra service sẽ throw AppException
            assertThatThrownBy(() ->
                    productService.addNewProduct(req))

                    // Kiểm tra đúng kiểu exception
                    .isInstanceOf(AppException.class)

                    // Kiểm tra đúng error code
                    .satisfies(ex ->
                            assertThat(((AppException) ex).getErrorCode())
                                    .isEqualTo(ErrorCode.USER_EXISTED));

            // Verify:
            // Đảm bảo repository.save()
            // KHÔNG được gọi
            // vì dữ liệu đã bị reject trước đó
            verify(productRepository, never())
                    .save(any());
        }

        @Test
        @DisplayName("TC_PS_ANP_02 – Category không tồn tại → throw AppException ROLE_NOT_FOUND")
        void shouldThrowException_whenCategoryNotFound() {

            // =========================
            // Arrange
            // =========================

            // Tạo request thêm sản phẩm
            ProductRequest req =
                    buildRequest("Samsung S24", 99L);

            // Mock:
            // Tên chưa tồn tại
            when(productRepository.existsByName("Samsung S24"))
                    .thenReturn(false);

            // Mock mapper:
            // Convert request -> Product entity
            when(productMapper.toProduct(req))
                    .thenReturn(fakeProduct(null, "Samsung S24"));

            // Mock categoryRepository:
            // Không tìm thấy category với id = 99
            when(categoryRepository.findById(99L))
                    .thenReturn(Optional.empty());

            // =========================
            // Act + Assert
            // =========================

            // Kiểm tra service throw exception
            assertThatThrownBy(() ->
                    productService.addNewProduct(req))

                    // Kiểm tra đúng kiểu exception
                    .isInstanceOf(AppException.class)

                    // Kiểm tra đúng error code
                    .satisfies(ex ->
                            assertThat(((AppException) ex).getErrorCode())
                                    .isEqualTo(ErrorCode.ROLE_NOT_FOUND));
        }

        @Test
        @DisplayName("TC_PS_ANP_03 – Hợp lệ, không có ảnh và variants → save thành công")
        void shouldSaveProduct_whenValidRequestWithoutImages()
                throws IOException {

            // =========================
            // Arrange
            // =========================

            // Tạo request hợp lệ
            ProductRequest req =
                    buildRequest("Pixel 8", 1L);

            // Tạo product entity giả lập
            Product product =
                    fakeProduct(null, "Pixel 8");

            // Tạo category giả lập
            Category category = new Category();
            category.setId(1L);

            // Tạo response mong đợi
            ProductResponse expected =
                    new ProductResponse();
            expected.setName("Pixel 8");

            // Mock:
            // Tên chưa tồn tại trong DB
            when(productRepository.existsByName("Pixel 8"))
                    .thenReturn(false);

            // Mock mapper:
            // Convert request -> entity
            when(productMapper.toProduct(req))
                    .thenReturn(product);

            // Mock categoryRepository:
            // Tìm thấy category
            when(categoryRepository.findById(1L))
                    .thenReturn(Optional.of(category));

            // Mock save:
            // Khi save product
            // trả về product đã lưu
            when(productRepository.save(product))
                    .thenReturn(product);

            // Mock mapper:
            // Convert entity -> response
            when(productMapper.toProductResponse(product))
                    .thenReturn(expected);

            // =========================
            // Act
            // =========================

            // Gọi service thêm sản phẩm
            ProductResponse result =
                    productService.addNewProduct(req);

            // =========================
            // Assert
            // =========================

            // Kiểm tra tên sản phẩm trả về đúng
            assertThat(result.getName())
                    .isEqualTo("Pixel 8");

            // Verify:
            // Đảm bảo repository.save()
            // đã được gọi để lưu sản phẩm
            verify(productRepository)
                    .save(product);

            // Verify:
            // Vì request không có ảnh
            // nên cloudinaryService.uploadImage()
            // không được gọi
            verify(cloudinaryService, never())
                    .uploadImage(any());
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // 6. inactive – toggle status
    // ════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("inactive()")
    class InactiveTest {

        @Test
        @DisplayName("TC_PS_INA_01 – Status đang là 1 → chuyển sang 0")
        void shouldSetStatusToZero_whenCurrentStatusIsOne() {
            Product p = fakeProduct("p1", "iPhone");
            p.setStatus(1);

            when(productRepository.findById("p1")).thenReturn(Optional.of(p));

            productService.inactive("p1");

            assertThat(p.getStatus()).isEqualTo(0);
            verify(productRepository).save(p);
        }

        @Test
        @DisplayName("TC_PS_INA_02 – Status đang là 0 → chuyển sang 1")
        void shouldSetStatusToOne_whenCurrentStatusIsZero() {
            Product p = fakeProduct("p1", "iPhone");
            p.setStatus(0);

            when(productRepository.findById("p1")).thenReturn(Optional.of(p));

            productService.inactive("p1");

            assertThat(p.getStatus()).isEqualTo(1);
            verify(productRepository).save(p);
        }

        @Test
        @DisplayName("TC_PS_INA_03 – Product không tồn tại → throw RuntimeException")
        void shouldThrowRuntimeException_whenProductNotFound() {
            when(productRepository.findById("ghost")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.inactive("ghost"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("User not found");
        }
    }

    @Nested
    @DisplayName("updateProduct()")
    class UpdateProductTest {

        // =========================================================
        // Hàm hỗ trợ tạo request cơ bản
        // =========================================================
        private ProductRequest buildRequest(String name, Long categoryId) {

            ProductRequest req = new ProductRequest();

            req.setName(name);

            CategoryRequest category = new CategoryRequest();
            category.setId(categoryId);

            req.setCategory(category);

            return req;
        }

        // =========================================================
        // TC_PS_UP_01
        // Product không tồn tại
        // =========================================================
        @Test
        @DisplayName("TC_PS_UP_01 – productId không tồn tại → throw AppException")
        void shouldThrowException_whenProductNotFound() {

            // Arrange
            ProductRequest request = buildRequest("iPhone 15", 1L);

            // Mock repository:
            // Không tìm thấy product
            when(productRepository.findById("not-found"))
                    .thenReturn(Optional.empty());

            // Act + Assert
            assertThatThrownBy(() ->
                    productService.updateProduct("not-found", request))

                    .isInstanceOf(AppException.class)

                    .satisfies(ex ->
                            assertThat(((AppException) ex).getErrorCode())
                                    .isEqualTo(ErrorCode.USER_NOT_EXISTED));
        }

        // =========================================================
        // TC_PS_UP_02
        // Category không tồn tại
        // =========================================================
        @Test
        @DisplayName("TC_PS_UP_02 – Category không tồn tại → throw AppException")
        void shouldThrowException_whenCategoryNotFound() {

            // Arrange
            ProductRequest request = buildRequest("Samsung S24", 99L);

            Product product = fakeProduct("p1", "Old Product");

            // Mock:
            // Tìm thấy product
            when(productRepository.findById("p1"))
                    .thenReturn(Optional.of(product));

            // Mock:
            // Không tìm thấy category
            when(categoryRepository.findById(99L))
                    .thenReturn(Optional.empty());

            // Act + Assert
            assertThatThrownBy(() ->
                    productService.updateProduct("p1", request))

                    .isInstanceOf(AppException.class)

                    .satisfies(ex ->
                            assertThat(((AppException) ex).getErrorCode())
                                    .isEqualTo(ErrorCode.ROLE_NOT_FOUND));
        }

        // =========================================================
        // TC_PS_UP_03
        // Update thông tin cơ bản không có ảnh
        // =========================================================
        @Test
        @DisplayName("TC_PS_UP_03 – Update thông tin cơ bản thành công")
        void shouldUpdateBasicInfoSuccessfully() throws IOException {

            // Arrange
            ProductRequest request = buildRequest("iPhone 15 Pro", 1L);

            Product product = fakeProduct("p1", "Old Name");

            Category category = new Category();
            category.setId(1L);

            ProductResponse response = new ProductResponse();
            response.setName("iPhone 15 Pro");

            // Mock product tồn tại
            when(productRepository.findById("p1"))
                    .thenReturn(Optional.of(product));

            // Mock category tồn tại
            when(categoryRepository.findById(1L))
                    .thenReturn(Optional.of(category));

            // Mock save
            when(productRepository.save(product))
                    .thenReturn(product);

            // Mock mapper response
            when(productMapper.toProductResponse(product))
                    .thenReturn(response);

            // Act
            ProductResponse result =
                    productService.updateProduct("p1", request);

            // Assert
            assertThat(result).isNotNull();

            assertThat(result.getName())
                    .isEqualTo("iPhone 15 Pro");

            // Verify save được gọi
            verify(productRepository)
                    .save(product);

            // Không upload ảnh
            verify(cloudinaryService, never())
                    .uploadImage(any());
        }

        // =========================================================
        // TC_PS_UP_04
        // Upload ảnh đại diện mới
        // =========================================================
        @Test
        @DisplayName("TC_PS_UP_04 – Upload ảnh đại diện mới")
        void shouldUpdateThumbnailImageSuccessfully() throws IOException {

            // Arrange
            ProductRequest request = buildRequest("iPhone 15", 1L);

            MultipartFile image = mock(MultipartFile.class);

            request.setImage(image);

            Product product = fakeProduct("p1", "iPhone");

            Category category = new Category();
            category.setId(1L);

            ProductResponse response = new ProductResponse();

            // Mock
            when(productRepository.findById("p1"))
                    .thenReturn(Optional.of(product));

            when(categoryRepository.findById(1L))
                    .thenReturn(Optional.of(category));

            // Mock upload cloudinary
            when(cloudinaryService.uploadImage(image))
                    .thenReturn("http://cloudinary/new-image.jpg");

            when(productRepository.save(product))
                    .thenReturn(product);

            when(productMapper.toProductResponse(product))
                    .thenReturn(response);

            // Act
            productService.updateProduct("p1", request);

            // Assert
            assertThat(product.getImage())
                    .isEqualTo("http://cloudinary/new-image.jpg");

            verify(cloudinaryService)
                    .uploadImage(image);
        }

        // =========================================================
        // TC_PS_UP_05
        // Update intro image cũ
        // =========================================================
        @Test
        @DisplayName("TC_PS_UP_05 – Update intro image cũ")
        void shouldUpdateOldIntroImage() throws IOException {

            // Arrange
            Product product = fakeProduct("p1", "iPhone");

            ProductImage oldImage = new ProductImage();
            oldImage.setId("img1");
            oldImage.setUrl("old-url");

            product.setIntroImages(new ArrayList<>(List.of(oldImage)));

            ProductRequest request = buildRequest("iPhone", 1L);

            ProductImageRequest imgReq = new ProductImageRequest();

            MultipartFile file = mock(MultipartFile.class);

            imgReq.setId("img1");
            imgReq.setFile(file);

            request.setIntroImages(List.of(imgReq));

            Category category = new Category();
            category.setId(1L);

            // Mock
            when(productRepository.findById("p1"))
                    .thenReturn(Optional.of(product));

            when(categoryRepository.findById(1L))
                    .thenReturn(Optional.of(category));

            when(cloudinaryService.uploadImage(file))
                    .thenReturn("new-url");

            when(productRepository.save(product))
                    .thenReturn(product);

            when(productMapper.toProductResponse(product))
                    .thenReturn(new ProductResponse());

            // Act
            productService.updateProduct("p1", request);

            // Assert
            assertThat(product.getIntroImages())
                    .hasSize(1);

            assertThat(product.getIntroImages().get(0).getUrl())
                    .isEqualTo("new-url");
        }

        // =========================================================
        // TC_PS_UP_06
        // Update variant cũ
        // =========================================================
        @Test
        @DisplayName("TC_PS_UP_06 – Update variant cũ")
        void shouldUpdateOldVariant() throws IOException {

            // Arrange
            Product product = fakeProduct("p1", "iPhone");

            ProductVariant variant = new ProductVariant();
            variant.setId("v1");
            variant.setColor("Black");

            product.setVariants(new ArrayList<>(List.of(variant)));

            ProductVariantRequest reqVar =
                    new ProductVariantRequest();

            reqVar.setId("v1");
            reqVar.setColor("Blue");

            ProductRequest request =
                    buildRequest("iPhone", 1L);

            request.setVariants(List.of(reqVar));

            Category category = new Category();
            category.setId(1L);

            // Mock
            when(productRepository.findById("p1"))
                    .thenReturn(Optional.of(product));

            when(categoryRepository.findById(1L))
                    .thenReturn(Optional.of(category));

            when(productRepository.save(product))
                    .thenReturn(product);

            when(productMapper.toProductResponse(product))
                    .thenReturn(new ProductResponse());

            // Act
            productService.updateProduct("p1", request);

            // Assert
            assertThat(product.getVariants())
                    .hasSize(1);

            assertThat(product.getVariants().get(0).getColor())
                    .isEqualTo("Blue");
        }

        // =========================================================
        // TC_PS_UP_07
        // Thêm mới variant
        // =========================================================
        @Test
        @DisplayName("TC_PS_UP_07 – Thêm mới variant")
        void shouldAddNewVariantSuccessfully() throws IOException {

            // Arrange
            Product product = fakeProduct("p1", "iPhone");

            product.setVariants(new ArrayList<>());

            ProductVariantRequest reqVar =
                    new ProductVariantRequest();

            reqVar.setColor("Red");

            ProductVariant newVariant =
                    new ProductVariant();

            newVariant.setColor("Red");

            ProductRequest request =
                    buildRequest("iPhone", 1L);

            request.setVariants(List.of(reqVar));

            Category category = new Category();
            category.setId(1L);

            // Mock
            when(productRepository.findById("p1"))
                    .thenReturn(Optional.of(product));

            when(categoryRepository.findById(1L))
                    .thenReturn(Optional.of(category));

            when(productVariantMapper.toVariant(reqVar))
                    .thenReturn(newVariant);

            when(productRepository.save(product))
                    .thenReturn(product);

            when(productMapper.toProductResponse(product))
                    .thenReturn(new ProductResponse());

            // Act
            productService.updateProduct("p1", request);

            // Assert
            assertThat(product.getVariants())
                    .hasSize(1);

            assertThat(product.getVariants().get(0).getColor())
                    .isEqualTo("Red");
        }

        // =========================================================
        // TC_PS_UP_08
        // Update attribute cũ
        // =========================================================
        @Test
        @DisplayName("TC_PS_UP_08 – Update attribute cũ")
        void shouldUpdateOldAttributeSuccessfully() throws IOException {

            // Arrange
            ProductAttribute attr =
                    new ProductAttribute();

            attr.setId("a1");
            attr.setName("128GB");

            ProductVariant variant =
                    new ProductVariant();

            variant.setId("v1");
            variant.setAttributes(
                    new ArrayList<>(List.of(attr)));

            Product product =
                    fakeProduct("p1", "iPhone");

            product.setVariants(
                    new ArrayList<>(List.of(variant)));

            ProductAttributeRequest attrReq =
                    new ProductAttributeRequest();

            attrReq.setId("a1");
            attrReq.setName("256GB");

            ProductVariantRequest reqVar =
                    new ProductVariantRequest();

            reqVar.setId("v1");
            reqVar.setAttributes(List.of(attrReq));

            ProductRequest request =
                    buildRequest("iPhone", 1L);

            request.setVariants(List.of(reqVar));

            Category category = new Category();
            category.setId(1L);

            // Mock
            when(productRepository.findById("p1"))
                    .thenReturn(Optional.of(product));

            when(categoryRepository.findById(1L))
                    .thenReturn(Optional.of(category));

            when(productRepository.save(product))
                    .thenReturn(product);

            when(productMapper.toProductResponse(product))
                    .thenReturn(new ProductResponse());

            // Act
            productService.updateProduct("p1", request);

            // Assert
            assertThat(
                    product.getVariants()
                            .get(0)
                            .getAttributes()
                            .get(0)
                            .getName())
                    .isEqualTo("256GB");
        }

        // =========================================================
        // TC_PS_UP_09
        // Thêm mới attribute
        // =========================================================
        @Test
        @DisplayName("TC_PS_UP_09 – Thêm mới attribute")
        void shouldAddNewAttributeSuccessfully() throws IOException {

            // Arrange
            ProductVariant variant =
                    new ProductVariant();

            variant.setId("v1");
            variant.setAttributes(new ArrayList<>());

            Product product =
                    fakeProduct("p1", "iPhone");

            product.setVariants(
                    new ArrayList<>(List.of(variant)));

            ProductAttributeRequest attrReq =
                    new ProductAttributeRequest();

            attrReq.setName("512GB");

            ProductAttribute attr =
                    new ProductAttribute();

            attr.setName("512GB");

            ProductVariantRequest reqVar =
                    new ProductVariantRequest();

            reqVar.setId("v1");
            reqVar.setAttributes(List.of(attrReq));

            ProductRequest request =
                    buildRequest("iPhone", 1L);

            request.setVariants(List.of(reqVar));

            Category category = new Category();
            category.setId(1L);

            // Mock
            when(productRepository.findById("p1"))
                    .thenReturn(Optional.of(product));

            when(categoryRepository.findById(1L))
                    .thenReturn(Optional.of(category));

            when(productAttributeMapper.toProductAttribute(attrReq))
                    .thenReturn(attr);

            when(productRepository.save(product))
                    .thenReturn(product);

            when(productMapper.toProductResponse(product))
                    .thenReturn(new ProductResponse());

            // Act
            productService.updateProduct("p1", request);

            // Assert
            assertThat(
                    product.getVariants()
                            .get(0)
                            .getAttributes())
                    .hasSize(1);

            assertThat(
                    product.getVariants()
                            .get(0)
                            .getAttributes()
                            .get(0)
                            .getName())
                    .isEqualTo("512GB");
        }

        // =========================================================
        // TC_PS_UP_10
        // Update trùng tên sản phẩm đã tồn tại
        // =========================================================
        @Test
        @DisplayName("TC_PS_UP_10 – Update trùng tên sản phẩm → throw AppException")
        void shouldThrowException_whenUpdateDuplicateProductName()
                throws IOException {

            // =========================
            // Arrange - Chuẩn bị dữ liệu test
            // =========================

            // Tạo request update
            // Người dùng muốn đổi tên product thành "iPhone 15"
            ProductRequest request =
                    buildRequest("iPhone 15", 1L);

            // Product hiện tại trong database
            Product product =
                    fakeProduct("p1", "Samsung S24");

            // Mock:
            // Tìm thấy product cần update
            when(productRepository.findById("p1"))
                    .thenReturn(Optional.of(product));

            // Mock:
            // Repository kiểm tra đã tồn tại product khác
            // có cùng tên "iPhone 15"
            when(productRepository.existsByName("iPhone 15"))
                    .thenReturn(true);

            // =========================
            // Act + Assert
            // =========================

            // Kiểm tra service sẽ throw exception
            // do tên sản phẩm bị trùng
            assertThatThrownBy(() ->
                    productService.updateProduct("p1", request))

                    // Kiểm tra đúng kiểu exception
                    .isInstanceOf(AppException.class)

                    // Kiểm tra đúng mã lỗi
                    .satisfies(ex ->
                            assertThat(((AppException) ex).getErrorCode())
                                    .isEqualTo(ErrorCode.USER_EXISTED));

            // =========================
            // Verify
            // =========================

            // Đảm bảo productRepository.save()
            // KHÔNG được gọi
            // vì update thất bại trước khi save
            verify(productRepository, never())
                    .save(any());

            // Đảm bảo không upload ảnh
            verify(cloudinaryService, never())
                    .uploadImage(any());
        }
        
        // =========================================================
        // TC_PS_UP_11
        // Update thành công
        // =========================================================
        @Test
        @DisplayName("TC_PS_UP_11 – Update thành công")
        void shouldUpdateProductSuccessfully() throws IOException {

            // Arrange
            ProductRequest request =
                    buildRequest("iPhone 15 Pro Max", 1L);

            Product product =
                    fakeProduct("p1", "Old Name");

            Category category = new Category();
            category.setId(1L);

            ProductResponse response =
                    new ProductResponse();

            response.setName("iPhone 15 Pro Max");

            // Mock
            when(productRepository.findById("p1"))
                    .thenReturn(Optional.of(product));

            when(categoryRepository.findById(1L))
                    .thenReturn(Optional.of(category));

            when(productRepository.save(product))
                    .thenReturn(product);

            when(productMapper.toProductResponse(product))
                    .thenReturn(response);

            // Act
            ProductResponse result =
                    productService.updateProduct("p1", request);

            // Assert
            assertThat(result).isNotNull();

            assertThat(result.getName())
                    .isEqualTo("iPhone 15 Pro Max");

            verify(productRepository)
                    .save(product);
        }

    }
}