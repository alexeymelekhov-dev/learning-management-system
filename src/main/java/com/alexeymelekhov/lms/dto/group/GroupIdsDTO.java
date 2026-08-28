package com.alexeymelekhov.lms.dto.group;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record GroupIdsDTO(
        @NotEmpty
        Set<@NotNull Long> groupIds
) {
}
