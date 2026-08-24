package com.alexeymelekhov.lms.dto.common;

import java.util.Map;

public record ErrorResponseDTO(
        int status,
        String message,
        Map<String, String> errors
) {
}