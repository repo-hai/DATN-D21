package com.DATN.Bej.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BannerResponse {
    String id;
    String title;
    String description;
    String imageUrl;
    String linkUrl;
    Integer displayOrder;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}

