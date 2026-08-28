package com.alexeymelekhov.lms.dto.student;

import java.util.Set;

public record StudentDTO(
        Long id,
        String firstName,
        String lastName,
        Set<Long> groups
) {
}
