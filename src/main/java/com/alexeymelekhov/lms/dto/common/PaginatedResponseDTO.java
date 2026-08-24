package com.alexeymelekhov.lms.dto.common;

import java.util.List;

public record PaginatedResponseDTO<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int page,
        int size
) {
}
