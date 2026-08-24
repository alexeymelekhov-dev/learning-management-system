package com.alexeymelekhov.lms.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseCreateDTO(
        @NotBlank
        @Size(min = 3, max = 150)
        String name,

        @NotBlank
        @Size(min = 10, max = 2000)
        String description
) {
}
