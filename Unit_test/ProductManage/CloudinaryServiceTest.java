package com.DATN.Bej;

import com.DATN.Bej.service.CloudinaryService;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;  
import static org.mockito.Mockito.lenient;
import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setup() {
        lenient().when(cloudinary.uploader())
                .thenReturn(uploader);
    }

    // =========================================================
    // uploadImage()
    // =========================================================
    @Nested
    @DisplayName("uploadImage()")
    class UploadImageTest {

        @Test
        @DisplayName("TC_CS_UI_01 – Upload ảnh hợp lệ")
        void shouldUploadImageSuccessfully()
                throws IOException {

            // =========================
            // Arrange
            // =========================

            MockMultipartFile file =
                    new MockMultipartFile(
                            "file",
                            "image.jpg",
                            "image/jpeg",
                            "hello".getBytes()
                    );

            Map<String, Object> uploadResult =
                    Map.of(
                            "secure_url",
                            "https://cloudinary.com/image.jpg"
                    );

            // Mock cloudinary upload
            when(uploader.upload(
                    any(byte[].class),
                    anyMap()
            )).thenReturn(uploadResult);

            // =========================
            // Act
            // =========================

            String result =
                    cloudinaryService.uploadImage(file);

            // =========================
            // Assert
            // =========================

            assertThat(result)
                    .isEqualTo(
                            "https://cloudinary.com/image.jpg"
                    );

            verify(uploader)
                    .upload(any(byte[].class), anyMap());
        }

        @Test
        @DisplayName("TC_CS_UI_02 – Upload ảnh null")
        void shouldThrowException_whenFileIsNull() {

            // =========================
            // Act + Assert
            // =========================

            assertThatThrownBy(() ->
                    cloudinaryService.uploadImage(null))

                    .isInstanceOf(
                            IllegalArgumentException.class
                    )

                    .hasMessage(
                            "File is null or empty"
                    );
        }

        @Test
        @DisplayName("TC_CS_UI_03 – Upload file rỗng")
        void shouldThrowException_whenFileIsEmpty() {

            // =========================
            // Arrange
            // =========================

            MockMultipartFile emptyFile =
                    new MockMultipartFile(
                            "file",
                            "image.jpg",
                            "image/jpeg",
                            new byte[0]
                    );

            // =========================
            // Act + Assert
            // =========================

            assertThatThrownBy(() ->
                    cloudinaryService.uploadImage(emptyFile))

                    .isInstanceOf(
                            IllegalArgumentException.class
                    )

                    .hasMessage(
                            "File is null or empty"
                    );
        }

        @Test
        @DisplayName("TC_CS_UI_04 – Upload file không phải ảnh")
        void shouldStillUpload_whenFileIsNotImage()
                throws IOException {

            // NOTE:
            // Service hiện tại KHÔNG validate mime type

            // =========================
            // Arrange
            // =========================

            MockMultipartFile file =
                    new MockMultipartFile(
                            "file",
                            "image.doc",
                            "application/msword",
                            "hello".getBytes()
                    );

            Map<String, Object> uploadResult =
                    Map.of(
                            "secure_url",
                            "https://cloudinary.com/image.doc"
                    );

            when(uploader.upload(
                    any(byte[].class),
                    anyMap()
            )).thenReturn(uploadResult);

            // =========================
            // Act
            // =========================

            String result =
                    cloudinaryService.uploadImage(file);

            // =========================
            // Assert
            // =========================

            assertThat(result)
                    .contains("cloudinary.com");
        }

        @Test
        @DisplayName("TC_CS_UI_05 – Upload file trùng tên")
        void shouldUploadSuccessfully_whenFilenameAlreadyExists()
                throws IOException {

            // =========================
            // Arrange
            // =========================

            MockMultipartFile file1 =
                    new MockMultipartFile(
                            "file",
                            "same.jpg",
                            "image/jpeg",
                            "hello".getBytes()
                    );

            MockMultipartFile file2 =
                    new MockMultipartFile(
                            "file",
                            "same.jpg",
                            "image/jpeg",
                            "world".getBytes()
                    );

            when(uploader.upload(
                    any(byte[].class),
                    anyMap()
            ))
                    .thenReturn(
                            Map.of(
                                    "secure_url",
                                    "https://cloudinary.com/1.jpg"
                            )
                    )
                    .thenReturn(
                            Map.of(
                                    "secure_url",
                                    "https://cloudinary.com/2.jpg"
                            )
                    );

            // =========================
            // Act
            // =========================

            String result1 =
                    cloudinaryService.uploadImage(file1);

            String result2 =
                    cloudinaryService.uploadImage(file2);

            // =========================
            // Assert
            // =========================

            assertThat(result1)
                    .isNotEqualTo(result2);
        }
    }

    // =========================================================
    // deleteImage()
    // =========================================================
    @Nested
    @DisplayName("deleteImage()")
    class DeleteImageTest {

        @Test
        @DisplayName("TC_CS_DI_01 – Xóa ảnh tồn tại")
        void shouldDeleteImageSuccessfully()
                throws IOException {

            // =========================
            // Arrange
            // =========================

            when(uploader.destroy(
                    eq("abc.jpg"),
                    anyMap()
            )).thenReturn(Map.of());

            // =========================
            // Act
            // =========================

            cloudinaryService.deleteImage("abc.jpg");

            // =========================
            // Assert
            // =========================

            verify(uploader)
                    .destroy(
                            eq("abc.jpg"),
                            anyMap()
                    );
        }

        @Test
        @DisplayName("TC_CS_DI_02 – Xóa ảnh không tồn tại")
        void shouldStillCallDestroy_whenImageNotFound()
                throws IOException {

            // =========================
            // Arrange
            // =========================

            when(uploader.destroy(
                    eq("notFound.jpg"),
                    anyMap()
            )).thenReturn(Map.of());

            // =========================
            // Act
            // =========================

            cloudinaryService.deleteImage(
                    "notFound.jpg"
            );

            // =========================
            // Assert
            // =========================

            verify(uploader)
                    .destroy(
                            eq("notFound.jpg"),
                            anyMap()
                    );
        }

        @Test
        @DisplayName("TC_CS_DI_03 – publicId null")
        void shouldDoNothing_whenPublicIdIsNull()
                throws IOException {

            // =========================
            // Act
            // =========================

            cloudinaryService.deleteImage(null);

            // =========================
            // Assert
            // =========================

            verify(uploader, never())
                    .destroy(anyString(), anyMap());
        }

        @Test
        @DisplayName("TC_CS_DI_04 – publicId rỗng")
        void shouldDoNothing_whenPublicIdIsBlank()
                throws IOException {

            // =========================
            // Act
            // =========================

            cloudinaryService.deleteImage("");

            // =========================
            // Assert
            // =========================

            verify(uploader, never())
                    .destroy(anyString(), anyMap());
        }
    }
}