package com.DATN.Bej;

import com.DATN.Bej.dto.request.productRequest.CategoryRequest;
import com.DATN.Bej.dto.response.productResponse.CategoryResponse;
import com.DATN.Bej.entity.product.Category;
import com.DATN.Bej.exception.AppException; 
import com.DATN.Bej.exception.ErrorCode;
import com.DATN.Bej.mapper.product.CategoryMapper;
import com.DATN.Bej.repository.product.CategoryRepository;
import com.DATN.Bej.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    // Mock repository
    @Mock
    private CategoryRepository categoryRepository;

    // Mock mapper
    @Mock
    private CategoryMapper categoryMapper;

    // Inject mock vào service
    @InjectMocks
    private CategoryService categoryService;

    @Nested
    @DisplayName("addNewCategory()")
    class AddNewCategoryTest {

        // =========================================================
        // Hàm hỗ trợ tạo request
        // =========================================================
        private CategoryRequest buildRequest(String name) {

            CategoryRequest request = new CategoryRequest();

            request.setName(name);

            return request;
        }

        // =========================================================
        // TC_CS_ANC_01
        // Thêm category thành công
        // =========================================================
        @Test
        @DisplayName("TC_CS_ANC_01 – Thêm category thành công")
        void shouldAddCategorySuccessfully() throws IOException {

            // =========================
            // Arrange
            // =========================

            // Tạo request hợp lệ
            CategoryRequest request =
                    buildRequest("Điện thoại");

            // Tạo entity category giả lập
            Category category = new Category();
            category.setName("Điện thoại");

            // Tạo response mong đợi
            CategoryResponse response =
                    new CategoryResponse();

            response.setName("Điện thoại");

            // Mock:
            // Tên category chưa tồn tại
            when(categoryRepository.existsByName("Điện thoại"))
                    .thenReturn(false);

            // Mock mapper:
            // Convert request -> entity
            when(categoryMapper.toCategory(request))
                    .thenReturn(category);

            // Mock save:
            // Khi save thì trả về category
            when(categoryRepository.save(category))
                    .thenReturn(category);

            // Mock mapper:
            // Convert entity -> response
            when(categoryMapper.toCategoryResponse(category))
                    .thenReturn(response);

            // =========================
            // Act
            // =========================

            CategoryResponse result =
                    categoryService.addNewCategory(request);

            // =========================
            // Assert
            // =========================

            // Kiểm tra kết quả không null
            assertThat(result).isNotNull();

            // Kiểm tra đúng tên category
            assertThat(result.getName())
                    .isEqualTo("Điện thoại");

            // Verify:
            // Đảm bảo save đã được gọi
            verify(categoryRepository)
                    .save(category);
        }

        // =========================================================
        // TC_CS_ANC_02
        // Tên category đã tồn tại
        // =========================================================
        @Test
        @DisplayName("TC_CS_ANC_02 – Tên category đã tồn tại → throw AppException")
        void shouldThrowException_whenCategoryNameAlreadyExists() {

            // =========================
            // Arrange
            // =========================

            // Tạo request với tên đã tồn tại
            CategoryRequest request =
                    buildRequest("Laptop");

            // Mock:
            // Tên category đã tồn tại trong DB
            when(categoryRepository.existsByName("Laptop"))
                    .thenReturn(true);

            // =========================
            // Act + Assert
            // =========================

            // Kiểm tra service throw exception
            assertThatThrownBy(() ->
                    categoryService.addNewCategory(request))

                    // Kiểm tra đúng kiểu exception
                    .isInstanceOf(AppException.class)

                    // Kiểm tra đúng mã lỗi
                    .satisfies(ex ->
                            assertThat(((AppException) ex).getErrorCode())
                                    .isEqualTo(ErrorCode.USER_EXISTED));

            // Verify:
            // Không được save xuống DB
            verify(categoryRepository, never())
                    .save(any());
        }

        // =========================================================
        // TC_CS_ANC_03
        // name = null
        // =========================================================
        @Test
        @DisplayName("TC_CS_ANC_03 – name = null")
        void shouldThrowException_whenNameIsNull() {

            // =========================
            // Arrange
            // =========================

            // Tạo request với name = null
            CategoryRequest request =
                    buildRequest(null);

            // =========================
            // Act + Assert
            // =========================

            // Kiểm tra exception
            // (phụ thuộc validation thực tế)
            assertThatThrownBy(() ->
                    categoryService.addNewCategory(request))

                    .isInstanceOf(Exception.class);

            // Verify:
            // Không save dữ liệu
            verify(categoryRepository, never())
                    .save(any());
        }

        // =========================================================
        // TC_CS_ANC_04
        // name là khoảng trắng
        // =========================================================
        @Test
        @DisplayName("TC_CS_ANC_04 – name là khoảng trắng")
        void shouldSaveCategory_whenNameIsBlank()
                throws IOException {

            // =========================
            // Arrange
            // =========================

            // Tạo request với name = "   "
            CategoryRequest request =
                    buildRequest("   ");

            // Tạo category entity
            Category category = new Category();
            category.setName("   ");

            // Tạo response
            CategoryResponse response =
                    new CategoryResponse();

            response.setName("   ");

            // Mock:
            // Tên chưa tồn tại
            when(categoryRepository.existsByName("   "))
                    .thenReturn(false);

            // Mock mapper
            when(categoryMapper.toCategory(request))
                    .thenReturn(category);

            // Mock save
            when(categoryRepository.save(category))
                    .thenReturn(category);

            // Mock mapper response
            when(categoryMapper.toCategoryResponse(category))
                    .thenReturn(response);

            // =========================
            // Act
            // =========================

            CategoryResponse result =
                    categoryService.addNewCategory(request);

            // =========================
            // Assert
            // =========================

            // Kiểm tra vẫn save thành công
            assertThat(result).isNotNull();

            // Kiểm tra name đúng
            assertThat(result.getName())
                    .isEqualTo("   ");

            // Verify save được gọi
            verify(categoryRepository)
                    .save(category);
        }

        // =========================================================
        // TC_CS_ANC_05
        // name quá dài >255 ký tự
        // =========================================================
        @Test
        @DisplayName("TC_CS_ANC_05 – name quá dài → throw Exception")
        void shouldThrowException_whenNameTooLong() {

            // =========================
            // Arrange
            // =========================

            // Tạo chuỗi rất dài
            String longName =
                    "abc".repeat(100);

            // Tạo request
            CategoryRequest request =
                    buildRequest(longName);

            // Mock:
            // Tên chưa tồn tại
            when(categoryRepository.existsByName(longName))
                    .thenReturn(false);

            // =========================
            // Act + Assert
            // =========================

            // Kiểm tra exception
            // (thường do validation hoặc DB constraint)
            assertThatThrownBy(() ->
                    categoryService.addNewCategory(request))

                    .isInstanceOf(Exception.class);

            // Verify:
            // Không save xuống DB
            verify(categoryRepository, never())
                    .save(any());
        }
    }
}
