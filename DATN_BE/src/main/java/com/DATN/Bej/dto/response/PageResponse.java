package com.DATN.Bej.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * DTO cho pagination response
 * @param <T> Type của items trong page
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {
    List<T> content;           // Danh sách items trong page hiện tại
    int page;                   // Số trang hiện tại (0-based)
    int size;                   // Số items mỗi trang
    long totalElements;         // Tổng số items
    int totalPages;             // Tổng số trang
    boolean first;              // Có phải trang đầu tiên không
    boolean last;               // Có phải trang cuối cùng không
    boolean hasNext;            // Có trang tiếp theo không
    boolean hasPrevious;        // Có trang trước đó không
}

