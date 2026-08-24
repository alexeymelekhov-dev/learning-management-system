package com.alexeymelekhov.lms.dto.schedule;

import java.time.LocalDateTime;

public record ScheduleDTO(
        Long id,
        Long groupId,
        Long teacherId,
        Long courseId,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}