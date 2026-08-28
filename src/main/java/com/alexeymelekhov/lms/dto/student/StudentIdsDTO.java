package com.alexeymelekhov.lms.dto.student;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record StudentIdsDTO(
        @NotEmpty
        Set<@NotNull Long> studentIds
) {
}
