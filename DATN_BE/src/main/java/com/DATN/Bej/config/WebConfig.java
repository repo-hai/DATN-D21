package com.DATN.Bej.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ưu tiên: src/main/resources/static/images (development)
        Path devPath = Paths.get("src/main/resources/static/images").toAbsolutePath();
        Path prodPath = Paths.get("uploads/images").toAbsolutePath();
        
        Path uploadPath;
        if (Files.exists(devPath.getParent()) || devPath.getParent().toFile().getParentFile().exists()) {
            uploadPath = devPath;
        } else {
            uploadPath = prodPath;
        }
        
        String uploadPathStr = uploadPath.toString().replace("\\", "/");
        if (!uploadPathStr.endsWith("/")) {
            uploadPathStr += "/";
        }
        
        registry.addResourceHandler("/images/**")  // URL pattern: /bej3/images/**
                .addResourceLocations("file:" + uploadPathStr)  // Serve từ thư mục images
                .addResourceLocations("classpath:/static/images/");  // Fallback: từ classpath
        
        log.info("📁 Serving static images from: {}", uploadPath.toAbsolutePath());
    }
    
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WebConfig.class);
}