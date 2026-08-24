package com.alexeymelekhov.lms.controller.api.v1;

import com.alexeymelekhov.lms.dto.course.CourseCreateDTO;
import com.alexeymelekhov.lms.dto.course.CourseDTO;
import com.alexeymelekhov.lms.dto.course.CourseUpdateDTO;
import com.alexeymelekhov.lms.dto.group.GroupIdsDTO;
import com.alexeymelekhov.lms.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course management")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @Operation(summary = "Create a course")
    public ResponseEntity<CourseDTO> createCourse(
            @RequestBody @Valid CourseCreateDTO dto
    ) {
        CourseDTO courseDTO = courseService.createCourse(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(courseDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a course")
    public CourseDTO updateCourse(
            @PathVariable Long id,
            @RequestBody @Valid CourseUpdateDTO dto
    ) {
        return courseService.updateCourse(id, dto);
    }

    @PostMapping("/{courseId}/groups")
    @Operation(summary = "Add courses into a group")
    public ResponseEntity<Void> addGroupsToCourse(
            @PathVariable Long courseId,
            @RequestBody @Valid GroupIdsDTO dto
            ) {
        courseService.addGroupsToCourse(courseId, dto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a course")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);

        return ResponseEntity.noContent().build();
    }
}

