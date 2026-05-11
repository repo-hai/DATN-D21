package com.DATN.Bej;

import com.DATN.Bej.service.FileStorageService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    @InjectMocks
    private FileStorageService fileStorageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() {

        // Inject giá trị cho các field @Value
        ReflectionTestUtils.setField(
                fileStorageService,
                "contextPath",
                "/bej3");

        ReflectionTestUtils.setField(
                fileStorageService,
                "serverPort",
                "8080");

        ReflectionTestUtils.setField(
                fileStorageService,
                "baseUrl",
                "http://localhost");

        // Gán thư mục temp để test
        ReflectionTestUtils.setField(
                fileStorageService,
                "uploadPath",
                tempDir);
    }

    // =========================================================
    // saveFile()
    // =========================================================
    @Nested
    @DisplayName("saveFile()")
    class SaveFileTest {

        @Test
        @DisplayName("TC_FSS_SF_01 – Lưu file hợp lệ")
        void shouldSaveFileSuccessfully()
                throws IOException {

            // =========================
            // Arrange
            // =========================

            MockMultipartFile file =
                    new MockMultipartFile(
                            "file",
                            "a.jpg",
                            "image/jpeg",
                            "hello".getBytes()
                    );

            // =========================
            // Act
            // =========================

            String result =
                    fileStorageService.saveFile(file);

            // =========================
            // Assert
            // =========================

            assertThat(result)
                    .isNotNull();

            assertThat(result)
                    .contains("/images/");

            assertThat(result)
                    .contains(".jpg");
        }

        @Test
        @DisplayName("TC_FSS_SF_02 – File null")
        void shouldThrowException_whenFileIsNull() {

            assertThatThrownBy(() ->
                    fileStorageService.saveFile(null))

                    .isInstanceOf(IllegalArgumentException.class)

                    .hasMessage("File is null or empty");
        }

        @Test
        @DisplayName("TC_FSS_SF_03 – File rỗng")
        void shouldThrowException_whenFileIsEmpty() {

            // =========================
            // Arrange
            // =========================

            MockMultipartFile file =
                    new MockMultipartFile(
                            "file",
                            "",
                            "image/jpeg",
                            new byte[0]
                    );

            // =========================
            // Act + Assert
            // =========================

            assertThatThrownBy(() ->
                    fileStorageService.saveFile(file))

                    .isInstanceOf(IllegalArgumentException.class)

                    .hasMessage("File is null or empty");
        }

        @Test
        @DisplayName("TC_FSS_SF_04 – File không có extension")
        void shouldAddDefaultExtension_whenFileHasNoExtension()
                throws IOException {

            // =========================
            // Arrange
            // =========================

            MockMultipartFile file =
                    new MockMultipartFile(
                            "file",
                            "image",
                            "image/jpeg",
                            "hello".getBytes()
                    );

            // =========================
            // Act
            // =========================

            String result =
                    fileStorageService.saveFile(file);

            // =========================
            // Assert
            // =========================

            assertThat(result)
                    .contains(".jpeg");
        }

        @Test
        @DisplayName("TC_FSS_SF_05 – File đã tồn tại")
        void shouldGenerateUniqueFilename_whenFileAlreadyExists()
                throws IOException {

            // =========================
            // Arrange
            // =========================

            MockMultipartFile file1 =
                    new MockMultipartFile(
                            "file",
                            "a.jpg",
                            "image/jpeg",
                            "hello".getBytes()
                    );

            MockMultipartFile file2 =
                    new MockMultipartFile(
                            "file",
                            "a.jpg",
                            "image/jpeg",
                            "world".getBytes()
                    );

            // =========================
            // Act
            // =========================

            String result1 =
                    fileStorageService.saveFile(file1);

            String result2 =
                    fileStorageService.saveFile(file2);

            // =========================
            // Assert
            // =========================

            assertThat(result1)
                    .isNotEqualTo(result2);
        }

        @Test
        @DisplayName("TC_FSS_SF_06 – File chứa ký tự đặc biệt")
        void shouldStillSave_whenFilenameContainsSpecialCharacters()
                throws IOException {

            // NOTE:
            // Service hiện tại không validate ký tự đặc biệt

            // =========================
            // Arrange
            // =========================

            MockMultipartFile file =
                    new MockMultipartFile(
                            "file",
                            "a#B.jpg",
                            "image/jpeg",
                            "hello".getBytes()
                    );

            // =========================
            // Act
            // =========================

            String result =
                    fileStorageService.saveFile(file);

            // =========================
            // Assert
            // =========================

            assertThat(result)
                    .isNotNull();
        }
    }

    // =========================================================
    // deleteFile()
    // =========================================================
    @Nested
    @DisplayName("deleteFile()")
    class DeleteFileTest {

        @Test
        @DisplayName("TC_FSS_DF_01 – Xóa file tồn tại")
        void shouldDeleteExistingFile()
                throws IOException {

            // =========================
            // Arrange
            // =========================

            // Set uploadPath cho service
            ReflectionTestUtils.setField(
                    fileStorageService,
                    "uploadPath",
                    tempDir
            );

            // Tạo file thật trong tempDir
            Path existingFile =
                    tempDir.resolve("abc.jpg");

            Files.createFile(existingFile);

            // Kiểm tra file tồn tại trước khi test
            assertThat(Files.exists(existingFile))
                    .isTrue();

            // =========================
            // Act
            // =========================

            boolean result =
                    fileStorageService.deleteFile("abc.jpg");

            // =========================
            // Assert
            // =========================

            // Kiểm tra delete thành công
            assertThat(result)
                    .isTrue();

            // Kiểm tra file đã bị xóa
            assertThat(Files.exists(existingFile))
                    .isFalse();
        }

        @Test
        @DisplayName("TC_FSS_DF_02 – Xóa file không tồn tại")
        void shouldReturnFalse_whenFileNotFound() {

            boolean result =
                    fileStorageService.deleteFile("notfound.jpg");

            assertThat(result)
                    .isFalse();
        }

        @Test
        @DisplayName("TC_FSS_DF_03 – Xóa file tên trống")
        void shouldReturnFalse_whenFilenameIsEmpty() {

            boolean result =
                    fileStorageService.deleteFile("");

            assertThat(result)
                    .isFalse();
        }
    }

    // =========================================================
    // getFilenameFromUrl()
    // =========================================================
    @Nested
    @DisplayName("getFilenameFromUrl()")
    class GetFilenameFromUrlTest {

        @Test
        @DisplayName("TC_FSS_GF_01 – URL hợp lệ")
        void shouldReturnFilenameFromValidUrl() {

            String result =
                    fileStorageService.getFilenameFromUrl(
                            "http://host/images/a.jpg"
                    );

            assertThat(result)
                    .isEqualTo("a.jpg");
        }

        @Test
        @DisplayName("TC_FSS_GF_02 – URL không có slash")
        void shouldReturnSameString_whenNoSlash() {

            String result =
                    fileStorageService.getFilenameFromUrl(
                            "a.jpg"
                    );

            assertThat(result)
                    .isEqualTo("a.jpg");
        }

        @Test
        @DisplayName("TC_FSS_GF_03 – URL null")
        void shouldReturnNull_whenUrlIsNull() {

            String result =
                    fileStorageService.getFilenameFromUrl(null);

            assertThat(result)
                    .isNull();
        }

        @Test
        @DisplayName("TC_FSS_GF_04 – URL empty")
        void shouldReturnNull_whenUrlIsEmpty() {

            String result =
                    fileStorageService.getFilenameFromUrl("");

            assertThat(result)
                    .isNull();
        }

        @Test
        @DisplayName("TC_FSS_GF_05 – URL ending slash")
        void shouldReturnOriginalUrl_whenEndingSlash() {

            String result =
                    fileStorageService.getFilenameFromUrl(
                            "http://host/images/"
                    );

            assertThat(result)
                    .isEqualTo("http://host/images/");
        }
    }
}