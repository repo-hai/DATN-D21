package com.DATN.Bej.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryService {

    Cloudinary cloudinary;

    /**
     * Upload 1 file ảnh lên Cloudinary và trả về URL (secure_url)
     */
    @SuppressWarnings("unchecked")
    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is null or empty");
        }

        log.info("Uploading image to Cloudinary: originalFilename={}", file.getOriginalFilename());

        Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "bej/products" 
                )
        );

        String secureUrl = (String) uploadResult.get("secure_url");
        log.info("Uploaded to Cloudinary, url={}", secureUrl);

        return secureUrl;
    }

    /**
     * Xoá ảnh trên Cloudinary theo public_id (nếu sau này bạn muốn dùng)
     */
    public void deleteImage(String publicId) throws IOException {
        if (publicId == null || publicId.isBlank()) {
            return;
        }

        log.info("Deleting image on Cloudinary: publicId={}", publicId);
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}


