package com.alexeymelekhov.lms.controller.api.v1;

import com.alexeymelekhov.lms.dto.student.StudentCreateDTO;
import com.alexeymelekhov.lms.dto.student.StudentDTO;
import com.alexeymelekhov.lms.dto.student.StudentUpdateDTO;
import com.alexeymelekhov.lms.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Student management")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @Operation(summary = "Create a student")
    public ResponseEntity<StudentDTO> createStudent(
            @RequestBody @Valid StudentCreateDTO dto
    ) {
        StudentDTO studentDTO = studentService.createStudent(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(studentDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a student")
    public StudentDTO updateStudent(
            @PathVariable Long id,
            @RequestBody @Valid StudentUpdateDTO dto
    ) {
        return studentService.updateStudent(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);

        return ResponseEntity.noContent().build();
    }
}
