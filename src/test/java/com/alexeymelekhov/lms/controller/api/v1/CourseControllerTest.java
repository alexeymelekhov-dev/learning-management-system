package com.alexeymelekhov.lms.controller.api.v1;

import com.alexeymelekhov.lms.AbstractIT;
import com.alexeymelekhov.lms.dto.common.ErrorResponseDTO;
import com.alexeymelekhov.lms.dto.course.CourseCreateDTO;
import com.alexeymelekhov.lms.dto.course.CourseDTO;
import com.alexeymelekhov.lms.dto.course.CourseUpdateDTO;
import com.alexeymelekhov.lms.dto.group.GroupIdsDTO;
import com.alexeymelekhov.lms.exception.ErrorMessage;
import com.alexeymelekhov.lms.model.Course;
import com.alexeymelekhov.lms.model.Group;
import com.alexeymelekhov.lms.model.Schedule;
import com.alexeymelekhov.lms.model.Teacher;
import com.alexeymelekhov.lms.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CourseControllerTest extends AbstractIT {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        scheduleRepository.deleteAll();
        groupRepository.deleteAllCourseRelations();
        courseRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        scheduleRepository.deleteAll();
        groupRepository.deleteAllCourseRelations();
        courseRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    @Test
    void shouldCreateCourse() {
        CourseCreateDTO request =
                new CourseCreateDTO("New course", "Course description");

        ResponseEntity<CourseDTO> response =
                restTemplate.postForEntity(
                        "/api/v1/courses",
                        request,
                        CourseDTO.class
                );

        assertEquals(
                HttpStatus.CREATED,
                response.getStatusCode()
        );
        assertNotNull(response.getBody());

        Course createdCourse = courseRepository
                .findById(response.getBody().id())
                .orElseThrow();

        assertEquals(
                "New course",
                createdCourse.getName()
        );
        assertEquals(
                "Course description",
                createdCourse.getDescription()
        );
    }

    @Test
    void shouldUpdateCourse() {
        Course course = courseRepository.save(
                new Course("Name", "Some description")
        );

        CourseUpdateDTO request =
                new CourseUpdateDTO("New name", "New description");

        ResponseEntity<CourseDTO> response =
                restTemplate.exchange(
                        "/api/v1/courses/" + course.getId(),
                        HttpMethod.PUT,
                        new HttpEntity<>(request),
                        CourseDTO.class
                );

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );
        assertNotNull(response.getBody());

        Course updatedCourse = courseRepository
                .findById(course.getId())
                .orElseThrow();

        assertEquals(
                "New name",
                updatedCourse.getName()
        );
        assertEquals(
                "New description",
                updatedCourse.getDescription()
        );
    }

    @Test
    void shouldAddGroupsToCourse() {
        Course course = courseRepository.save(new Course("New", "Description"));
        Group group = groupRepository.save(new Group("New"));

        GroupIdsDTO request = new GroupIdsDTO(Set.of(group.getId()));

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/v1/courses/" + course.getId() + "/groups",
                HttpMethod.POST,
                new HttpEntity<>(request),
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        Course updatedCourse = courseRepository
                .findWithGroupsById(course.getId())
                .orElseThrow();

        assertEquals(
                group.getId(),
                updatedCourse.getGroups().iterator().next().getId()
        );
    }

    @Test
    void shouldDeleteCourse() {
        Group group = groupRepository.save(new Group("group"));
        Course course = courseRepository.save(
                new Course("New", "Description", Set.of(group))
        );
        Teacher teacher = teacherRepository.save(new Teacher("Ivan", "Ivanov"));
        LocalDateTime startDate = LocalDateTime.now().plusHours(1);
        LocalDateTime endDate = startDate.plusHours(2);
        Schedule schedule = scheduleRepository.save(
                new Schedule(
                        group,
                        course,
                        teacher,
                        startDate,
                        endDate
                )
        );

        ResponseEntity<Void> response =
                restTemplate.exchange(
                        "/api/v1/courses/" + course.getId(),
                        HttpMethod.DELETE,
                        null,
                        Void.class
                );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        assertTrue(courseRepository.findById(course.getId()).isEmpty());
        assertTrue(scheduleRepository.findById(schedule.getId()).isEmpty());

        assertTrue(groupRepository.findById(group.getId()).isPresent());
    }

    @Test
    void shouldReturn400WhenCreateRequestIsInvalid() {
        CourseCreateDTO request =
                new CourseCreateDTO("New course", "");

        ResponseEntity<ErrorResponseDTO> response =
                restTemplate.postForEntity(
                        "/api/v1/courses",
                        request,
                        ErrorResponseDTO.class
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );
        assertNotNull(response.getBody());
        assertNotNull(
                response.getBody().errors().get("description")
        );
    }

    @Test
    void shouldReturn400WhenUpdateRequestIsInvalid() {
        Course course = courseRepository.save(
                new Course("Old name", "Old description")
        );

        CourseUpdateDTO request =
                new CourseUpdateDTO("New course", "");

        ResponseEntity<ErrorResponseDTO> response =
                restTemplate.exchange(
                        "/api/v1/courses/" + course.getId(),
                        HttpMethod.PUT,
                        new HttpEntity<>(request),
                        ErrorResponseDTO.class
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );
        assertNotNull(response.getBody());

        Course updatedCourse = courseRepository
                .findById(course.getId())
                .orElseThrow();

        assertNotNull(
                response.getBody().errors().get("description")
        );
        assertEquals(
                "Old name",
                updatedCourse.getName()
        );
        assertEquals(
                "Old description",
                updatedCourse.getDescription()
        );
    }

    @Test
    void shouldReturn404WhenAddingGroupAndGroupNotFound() {
        Course course = courseRepository.save(
                new Course("New", "Description")
        );

        ResponseEntity<CourseDTO> response =
                restTemplate.exchange(
                        "/api/v1/courses/"
                                + course.getId()
                                + "/groups/1",
                        HttpMethod.PUT,
                        null,
                        CourseDTO.class
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );
        assertNotNull(response.getBody());

        Course updatedCourse = courseRepository
                .findWithGroupsById(course.getId())
                .orElseThrow();

        assertEquals(
                0,
                updatedCourse.getGroups().size()
        );
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentCourse() {
        ResponseEntity<ErrorResponseDTO> response =
                restTemplate.exchange(
                        "/api/v1/courses/1",
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
                ErrorMessage.COURSE_NOT_FOUND.format(1),
                response.getBody().message()
        );
    }
}
