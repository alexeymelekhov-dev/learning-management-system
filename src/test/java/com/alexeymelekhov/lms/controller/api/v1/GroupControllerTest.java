package com.alexeymelekhov.lms.controller.api.v1;

import com.alexeymelekhov.lms.AbstractIT;
import com.alexeymelekhov.lms.dto.common.ErrorResponseDTO;
import com.alexeymelekhov.lms.dto.common.PaginatedResponseDTO;
import com.alexeymelekhov.lms.dto.group.GroupCreateDTO;
import com.alexeymelekhov.lms.dto.group.GroupDTO;
import com.alexeymelekhov.lms.dto.group.GroupUpdateDTO;
import com.alexeymelekhov.lms.dto.schedule.ScheduleCreateDTO;
import com.alexeymelekhov.lms.dto.schedule.ScheduleDTO;
import com.alexeymelekhov.lms.dto.schedule.ScheduleUpdateDTO;
import com.alexeymelekhov.lms.dto.student.StudentIdsDTO;
import com.alexeymelekhov.lms.exception.ErrorMessage;
import com.alexeymelekhov.lms.model.Course;
import com.alexeymelekhov.lms.model.Group;
import com.alexeymelekhov.lms.model.Schedule;
import com.alexeymelekhov.lms.model.Student;
import com.alexeymelekhov.lms.model.Teacher;
import com.alexeymelekhov.lms.repository.CourseRepository;
import com.alexeymelekhov.lms.repository.GroupRepository;
import com.alexeymelekhov.lms.repository.ScheduleRepository;
import com.alexeymelekhov.lms.repository.StudentRepository;
import com.alexeymelekhov.lms.repository.TeacherRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GroupControllerTest extends AbstractIT {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        scheduleRepository.deleteAll();
        groupRepository.deleteAll();
        courseRepository.deleteAll();
        teacherRepository.deleteAll();
        studentRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        scheduleRepository.deleteAll();
        groupRepository.deleteAll();
        courseRepository.deleteAll();
        teacherRepository.deleteAll();
        studentRepository.deleteAll();
    }

    @Test
    void shouldCreateGroup() {
        GroupCreateDTO request = new GroupCreateDTO("New group");

        ResponseEntity<GroupDTO> response = restTemplate.postForEntity(
                "/api/v1/groups",
                request,
                GroupDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        Group createdGroup = groupRepository
                .findById(response.getBody().id())
                .orElseThrow();

        assertEquals("New group", createdGroup.getName());
    }

    @Test
    void shouldUpdateGroup() {
        Group group = groupRepository.save(
                new Group("Old name")
        );

        GroupUpdateDTO request = new GroupUpdateDTO("New name");

        ResponseEntity<GroupDTO> response = restTemplate.exchange(
                "/api/v1/groups/" + group.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(request),
                GroupDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Group updatedGroup = groupRepository
                .findById(group.getId())
                .orElseThrow();

        assertEquals("New name", updatedGroup.getName());
    }

    @Test
    void shouldDeleteGroup() {
        Group group = groupRepository.save(new Group("New"));
        Course course = courseRepository.save(
                new Course("New course", "Description", Set.of(group))
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

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/v1/groups/" + group.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        assertTrue(courseRepository.findById(course.getId()).isPresent());
        assertTrue(teacherRepository.findById(teacher.getId()).isPresent());

        assertTrue(groupRepository.findById(group.getId()).isEmpty());
        assertTrue(scheduleRepository.findById(schedule.getId()).isEmpty());
    }

    @Test
    void shouldAddStudentsToGroup() {
        Student student = studentRepository.save(
                new Student("Ivan", "Ivanov")
        );

        Group group = groupRepository.save(
                new Group("New1", Set.of(student))
        );

        Student studentToAdd = studentRepository.save(
                new Student("Petr", "Petrov")
        );

        StudentIdsDTO request = new StudentIdsDTO(
                Set.of(studentToAdd.getId())
        );

        ResponseEntity<Void> response = restTemplate.postForEntity(
                "/api/v1/groups/" + group.getId() + "/students",
                request,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        Assertions.assertNull(response.getBody());

        Group updatedGroup = groupRepository
                .findGroupWithStudentsById(group.getId())
                .orElseThrow();

        Student addedStudent = updatedGroup.getStudents()
                .stream()
                .filter(s -> s.getId().equals(studentToAdd.getId()))
                .findFirst()
                .orElseThrow();

        assertEquals(2, updatedGroup.getStudents().size());
        assertEquals("Petrov", addedStudent.getLastName());
    }

    @Test
    void shouldGetGroupSchedules() {
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
                        "/api/v1/groups/" + group.getId() + "/schedules",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
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
    void shouldCreateScheduleForGroup() {
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

        ScheduleCreateDTO request = new ScheduleCreateDTO(
                teacher.getId(),
                course.getId(),
                startDate,
                endDate
        );

        ResponseEntity<ScheduleDTO> response = restTemplate.postForEntity(
                "/api/v1/groups/" + group.getId() + "/schedules",
                request,
                ScheduleDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Schedule createdSchedule = scheduleRepository
                .findById(response.getBody().id())
                .orElseThrow();

        assertEquals(
                response.getBody().id(),
                createdSchedule.getId()
        );

        assertEquals(
                group.getId(),
                createdSchedule.getGroup().getId()
        );

        assertEquals(
                course.getId(),
                createdSchedule.getCourse().getId()
        );

        assertEquals(
                teacher.getId(),
                createdSchedule.getTeacher().getId()
        );

        assertEquals(
                startDate,
                createdSchedule.getStartDate()
        );

        assertEquals(
                endDate,
                createdSchedule.getEndDate()
        );
    }

    @Test
    void shouldUpdateScheduleTimeForGroup() {
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

        Schedule schedule = scheduleRepository.save(
                new Schedule(
                        group,
                        course,
                        teacher,
                        startDate,
                        endDate
                )
        );

        LocalDateTime newStartDate = LocalDateTime.now().plusHours(2);
        LocalDateTime newEndDate = startDate.plusHours(3);

        ScheduleUpdateDTO request = new ScheduleUpdateDTO(
                newStartDate,
                newEndDate
        );

        ResponseEntity<ScheduleDTO> response = restTemplate.exchange(
                "/api/v1/groups/" + group.getId()
                        + "/schedules/" + schedule.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ScheduleDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Schedule updatedSchedule = scheduleRepository
                .findById(schedule.getId())
                .orElseThrow();

        assertEquals(
                newStartDate,
                updatedSchedule.getStartDate()
        );

        assertEquals(
                newEndDate,
                updatedSchedule.getEndDate()
        );
    }

    @Test
    void shouldDeleteScheduleForGroup() {
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

        Schedule schedule = scheduleRepository.save(
                new Schedule(
                        group,
                        course,
                        teacher,
                        startDate,
                        endDate
                )
        );

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/v1/groups/" + group.getId()
                        + "/schedules/" + schedule.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(
                HttpStatus.NO_CONTENT,
                response.getStatusCode()
        );

        Assertions.assertNull(response.getBody());

        assertTrue(
                scheduleRepository.findById(schedule.getId()).isEmpty()
        );
    }

    @Test
    void shouldReturn400WhenCreateRequestIsInvalid() {
        GroupCreateDTO request = new GroupCreateDTO("");

        ResponseEntity<ErrorResponseDTO> response =
                restTemplate.postForEntity(
                        "/api/v1/groups",
                        request,
                        ErrorResponseDTO.class
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertNotNull(
                response.getBody().errors().get("name")
        );
    }

    @Test
    void shouldReturn400WhenUpdateRequestIsInvalid() {
        Group group = groupRepository.save(
                new Group("Old name")
        );

        GroupUpdateDTO request = new GroupUpdateDTO("");

        ResponseEntity<ErrorResponseDTO> response = restTemplate.exchange(
                "/api/v1/groups/" + group.getId(),
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
                response.getBody().errors().get("name")
        );
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingGroup() {
        ResponseEntity<ErrorResponseDTO> response = restTemplate.exchange(
                "/api/v1/groups/1111",
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
                ErrorMessage.GROUPS_NOT_FOUND.format(1111),
                response.getBody().message()
        );
    }
}
