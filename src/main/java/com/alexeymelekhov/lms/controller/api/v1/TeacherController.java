package com.alexeymelekhov.lms.controller.api.v1;

import com.alexeymelekhov.lms.dto.common.PaginatedResponseDTO;
import com.alexeymelekhov.lms.dto.schedule.ScheduleDTO;
import com.alexeymelekhov.lms.dto.teacher.TeacherCreateDTO;
import com.alexeymelekhov.lms.dto.teacher.TeacherDTO;
import com.alexeymelekhov.lms.dto.teacher.TeacherUpdateDTO;
import com.alexeymelekhov.lms.service.ScheduleService;
import com.alexeymelekhov.lms.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
@Tag(name = "Teacher", description = "Teacher management")
public class TeacherController {

    private final TeacherService teacherService;
    private final ScheduleService scheduleService;

    @PostMapping
    @Operation(summary = "Create a teacher")
    public ResponseEntity<TeacherDTO> createTeacher(
            @RequestBody @Valid TeacherCreateDTO dto
    ) {
        TeacherDTO teacherDTO = teacherService.createTeacher(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(teacherDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a teacher")
    public TeacherDTO updateTeacher(
            @PathVariable Long id,
            @RequestBody @Valid TeacherUpdateDTO dto
    ) {
        return teacherService.updateTeacher(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a teacher")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/schedules")
    @Operation(summary = "Get teacher schedules")
    public PaginatedResponseDTO<ScheduleDTO> getTeacherSchedules(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return scheduleService.getSchedulesByTeacherId(id, pageable);
    }
}
