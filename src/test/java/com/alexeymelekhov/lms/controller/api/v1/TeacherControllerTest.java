package com.alexeymelekhov.lms.controller.api.v1;

import com.alexeymelekhov.lms.AbstractIT;
import com.alexeymelekhov.lms.dto.common.ErrorResponseDTO;
import com.alexeymelekhov.lms.dto.common.PaginatedResponseDTO;
import com.alexeymelekhov.lms.dto.schedule.ScheduleDTO;
import com.alexeymelekhov.lms.dto.teacher.TeacherCreateDTO;
import com.alexeymelekhov.lms.dto.teacher.TeacherDTO;
import com.alexeymelekhov.lms.dto.teacher.TeacherUpdateDTO;
import com.alexeymelekhov.lms.exception.ErrorMessage;
import com.alexeymelekhov.lms.model.Course;
import com.alexeymelekhov.lms.model.Group;
import com.alexeymelekhov.lms.model.Schedule;
import com.alexeymelekhov.lms.model.Teacher;
import com.alexeymelekhov.lms.repository.CourseRepository;
import com.alexeymelekhov.lms.repository.GroupRepository;
import com.alexeymelekhov.lms.repository.ScheduleRepository;
import com.alexeymelekhov.lms.repository.TeacherRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TeacherControllerTest extends AbstractIT {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateTeacher() {
        TeacherCreateDTO request = new TeacherCreateDTO("Ivan", "Ivanov");

        ResponseEntity<TeacherDTO> response = restTemplate.postForEntity(
                "/api/v1/teachers",
                request,
                TeacherDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        Teacher createdTeacher = teacherRepository
                .findById(response.getBody().id())
                .orElseThrow();

        assertEquals("Ivan", createdTeacher.getFirstName());
        assertEquals("Ivanov", createdTeacher.getLastName());
    }

    @Test
    void shouldUpdateTeacher() {
        Teacher teacher = teacherRepository.save(
                new Teacher("Ivan", "Ivanov")
        );

        TeacherUpdateDTO request = new TeacherUpdateDTO("Petr", "Petrov");

        ResponseEntity<TeacherDTO> response = restTemplate.exchange(
                "/api/v1/teachers/" + teacher.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(request),
                TeacherDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Teacher updatedTeacher = teacherRepository
                .findById(teacher.getId())
                .orElseThrow();

        assertEquals("Petr", updatedTeacher.getFirstName());
        assertEquals("Petrov", updatedTeacher.getLastName());
    }

    @Test
    void shouldDeleteTeacher() {
        Teacher teacher = teacherRepository.save(
                new Teacher("Ivan", "Ivanov")
        );

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/v1/teachers/" + teacher.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        Assertions.assertNull(response.getBody());
        Assertions.assertTrue(
                teacherRepository.findById(teacher.getId()).isEmpty()
        );
    }

    @Test
    void shouldGetTeacherSchedules() {
        Teacher teacher = teacherRepository.save(
                new Teacher("Ivan", "Ivanov")
        );

        Group group = groupRepository.save(
                new Group("Group")
        );

        Course course = courseRepository.save(
                new Course("Course", "Description course")
        );

        LocalDateTime startDate = LocalDateTime.now().plusHours(1);
        LocalDateTime endDate = startDate.plusHours(2);

        scheduleRepository.save(
                new Schedule(
                        group,
                        course,
                        teacher,
                        startDate,
                        endDate
                )
        );

        ResponseEntity<PaginatedResponseDTO<ScheduleDTO>> response =
                restTemplate.exchange(
                        "/api/v1/teachers/" + teacher.getId() + "/schedules",
                        HttpMethod.GET,
                        null,
                        new org.springframework.core.ParameterizedTypeReference<
                                PaginatedResponseDTO<ScheduleDTO>
                                >() {
                        }
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        PaginatedResponseDTO<ScheduleDTO> body = response.getBody();

        ScheduleDTO responseSchedule = body.content()
                .iterator()
                .next();

        assertEquals(
                teacher.getId(),
                responseSchedule.teacherId()
        );

        assertEquals(
                group.getId(),
                responseSchedule.groupId()
        );

        assertEquals(
                course.getId(),
                responseSchedule.courseId()
        );

        assertEquals(
                startDate,
                responseSchedule.startDate()
        );

        assertEquals(
                endDate,
                responseSchedule.endDate()
        );

        assertEquals(1L, body.totalElements());
        assertEquals(1, body.totalPages());
        assertEquals(0, body.page());
        assertEquals(20, body.size());
    }

    @Test
    void shouldReturn400WhenCreateRequestIsInvalid() {
        TeacherCreateDTO request = new TeacherCreateDTO("", "Ivanov");

        ResponseEntity<ErrorResponseDTO> response =
                restTemplate.postForEntity(
                        "/api/v1/teachers",
                        request,
                        ErrorResponseDTO.class
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertNotNull(
                response.getBody().errors().get("firstName")
        );

        Assertions.assertTrue(
                teacherRepository.findAll().isEmpty()
        );
    }

    @Test
    void shouldReturn400WhenUpdateRequestIsInvalid() {
        Teacher teacher = teacherRepository.save(
                new Teacher("Ivan", "Ivanov")
        );

        TeacherUpdateDTO request = new TeacherUpdateDTO("", "Petrov");

        ResponseEntity<ErrorResponseDTO> response =
                restTemplate.exchange(
                        "/api/v1/teachers/" + teacher.getId(),
                        HttpMethod.PUT,
                        new HttpEntity<>(request),
                        ErrorResponseDTO.class
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertNotNull(
                response.getBody().errors().get("firstName")
        );
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentTeacher() {
        ResponseEntity<ErrorResponseDTO> response =
                restTemplate.exchange(
                        "/api/v1/teachers/1111",
                        HttpMethod.DELETE,
                        null,
                        ErrorResponseDTO.class
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                ErrorMessage.TEACHER_NOT_FOUND.format(1111),
                response.getBody().message()
        );
    }
}