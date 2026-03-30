package com.DATN.Bej.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "banner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Banner {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    
    @Column(nullable = false)
    String title;
    
    @Column(columnDefinition = "TEXT")
    String description;
    
    @Column(nullable = false)
    String imageUrl;
    
    @Column
    String linkUrl; // URL để redirect khi click vào banner (optional)
    
    @Column(nullable = false)
    Integer displayOrder; // Thứ tự hiển thị (số nhỏ hơn hiển thị trước)
    
    @Column(nullable = false)
    Boolean isActive; // Banner có đang active không
    
    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt;
    
    @Column
    LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

