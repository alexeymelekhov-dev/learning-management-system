package com.alexeymelekhov.lms.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GroupCreateDTO(
        @NotBlank
        @Size(min = 2, max = 50)
        String name
) {
}
