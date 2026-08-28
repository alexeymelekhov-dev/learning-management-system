package com.alexeymelekhov.lms.dto.schedule;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ScheduleCreateDTO(
        @NotNull Long teacherId,
        @NotNull Long courseId,
        @NotNull @Future LocalDateTime startDate,
        @NotNull @Future LocalDateTime endDate
) {
}
