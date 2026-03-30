package com.DATN.Bej.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {
    
    @Value("${server.servlet.context-path:/bej3}")
    private String contextPath;
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${app.base-url:http://localhost}")
    private String baseUrl;
    
    // Thư mục lưu ảnh - ưu tiên src/main/resources/static/images (development)
    // Nếu không tồn tại thì dùng thư mục bên ngoài
    private Path uploadPath;
    
    /**
     * Khởi tạo đường dẫn lưu file
     * Ưu tiên: src/main/resources/static/images (development)
     * Fallback: uploads/images (production hoặc khi không có src)
     */
    private Path getUploadPath() {
        if (uploadPath == null) {
            // Thử dùng src/main/resources/static/images trước (development)
            Path devPath = Paths.get("src/main/resources/static/images").toAbsolutePath();
            if (Files.exists(devPath.getParent()) || devPath.getParent().toFile().getParentFile().exists()) {
                uploadPath = devPath;
                log.info("📁 Using development upload path: {}", uploadPath);
            } else {
                // Fallback: dùng thư mục uploads bên ngoài project
                uploadPath = Paths.get("uploads/images").toAbsolutePath();
                log.info("📁 Using production upload path: {}", uploadPath);
            }
            
            // Đảm bảo thư mục tồn tại và có quyền ghi
            ensureDirectoryExistsAndWritable(uploadPath);
        }
        return uploadPath;
    }
    
    /**
     * Đảm bảo thư mục tồn tại và có quyền ghi
     */
    private void ensureDirectoryExistsAndWritable(Path directory) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
                log.info("📁 Created directory: {}", directory.toAbsolutePath());
            }
            
            // Kiểm tra quyền ghi
            if (!Files.isWritable(directory)) {
                log.warn("⚠️ Directory may not be writable: {}", directory.toAbsolutePath());
                // Thử tạo file test để kiểm tra
                Path testFile = directory.resolve(".write-test");
                try {
                    Files.createFile(testFile);
                    Files.delete(testFile);
                    log.info("✅ Directory is writable: {}", directory.toAbsolutePath());
                } catch (IOException e) {
                    log.error("❌ Directory is NOT writable: {} - {}", directory.toAbsolutePath(), e.getMessage());
                    throw new IOException("Upload directory is not writable: " + directory.toAbsolutePath(), e);
                }
            } else {
                log.info("✅ Directory exists and is writable: {}", directory.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("❌ Error ensuring directory exists: {}", e.getMessage());
            throw new RuntimeException("Cannot create or access upload directory", e);
        }
    }
    
    /**
     * Lưu file và trả về URL để truy cập
     * @param file File cần lưu
     * @return URL đầy đủ để truy cập file
     * @throws IOException Nếu có lỗi khi lưu file
     */
    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is null or empty");
        }
        
        Path uploadDir = getUploadPath();
        
        // Tạo thư mục nếu chưa tồn tại
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
            log.info("📁 Created upload directory: {}", uploadDir.toAbsolutePath());
        }
        
        // Tạo tên file tự động: UUID + extension gốc
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        } else {
            // Nếu không có extension, thử lấy từ content type
            String contentType = file.getContentType();
            if (contentType != null && contentType.startsWith("image/")) {
                extension = "." + contentType.substring(6); // image/jpeg -> .jpeg
            } else {
                extension = ".jpg"; // Default
            }
        }
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        
        // Lưu file
        Path filePath = uploadDir.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        log.info("✅ File saved: {} -> {}", originalFilename, uniqueFilename);
        log.info("📂 File path: {}", filePath.toAbsolutePath());
        
        // Trả về URL để truy cập
        String fileUrl = String.format("%s:%s%s/images/%s", 
            baseUrl, serverPort, contextPath, uniqueFilename);
        
        log.info("📎 File URL: {}", fileUrl);
        return fileUrl;
    }
    
    /**
     * Xóa file
     * @param filename Tên file cần xóa (có thể là tên file hoặc URL)
     * @return true nếu xóa thành công
     */
    public boolean deleteFile(String filename) {
        try {
            // Nếu là URL, lấy tên file từ URL
            String actualFilename = getFilenameFromUrl(filename);
            if (actualFilename == null) {
                actualFilename = filename;
            }
            
            Path uploadDir = getUploadPath();
            Path filePath = uploadDir.resolve(actualFilename);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("🗑️ Deleted file: {}", actualFilename);
                return true;
            }
            log.warn("⚠️ File not found: {}", actualFilename);
            return false;
        } catch (IOException e) {
            log.error("❌ Error deleting file {}: {}", filename, e.getMessage());
            return false;
        }
    }
    
    /**
     * Lấy tên file từ URL
     * @param url URL của file
     * @return Tên file
     */
    public String getFilenameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        // Lấy phần cuối cùng sau dấu /
        int lastSlash = url.lastIndexOf("/");
        if (lastSlash >= 0 && lastSlash < url.length() - 1) {
            return url.substring(lastSlash + 1);
        }
        return url;
    }
}

