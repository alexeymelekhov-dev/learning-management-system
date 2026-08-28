package com.alexeymelekhov.lms.dto.schedule;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ScheduleUpdateDTO(
        @NotNull LocalDateTime startDate,
        @NotNull LocalDateTime endDate
) {
}
