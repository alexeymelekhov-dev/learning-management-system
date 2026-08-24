package com.alexeymelekhov.lms.controller.api.v1;

import com.alexeymelekhov.lms.dto.common.ErrorResponseDTO;
import com.alexeymelekhov.lms.dto.student.StudentCreateDTO;
import com.alexeymelekhov.lms.dto.student.StudentDTO;
import com.alexeymelekhov.lms.dto.student.StudentUpdateDTO;
import com.alexeymelekhov.lms.exception.ErrorMessage;
import com.alexeymelekhov.lms.model.Group;
import com.alexeymelekhov.lms.model.Student;
import com.alexeymelekhov.lms.repository.GroupRepository;
import com.alexeymelekhov.lms.repository.StudentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Testcontainers
class StudentControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private StudentRepository studentRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        groupRepository.deleteAll();
        studentRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        groupRepository.deleteAll();
        studentRepository.deleteAll();
    }

    @Test
    void shouldCreateStudent() {
        Group group = groupRepository.save(
                new Group("New group")
        );

        StudentCreateDTO request = new StudentCreateDTO(
                "Ivan",
                "Ivanov",
                Set.of(group.getId())
        );

        ResponseEntity<StudentDTO> response =
                restTemplate.postForEntity(
                        "/api/v1/students",
                        request,
                        StudentDTO.class
                );

        assertEquals(
                HttpStatus.CREATED,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        StudentDTO responseDto = response.getBody();

        assertNotNull(responseDto.id());
        Assertions.assertTrue(responseDto.id() > 0);
        assertEquals("Ivan", responseDto.firstName());
        assertEquals("Ivanov", responseDto.lastName());
        assertEquals(
                group.getId(),
                responseDto.groups().iterator().next()
        );

        Student student = studentRepository
                .findWithGroupsById(responseDto.id())
                .orElseThrow();

        assertEquals("Ivan", student.getFirstName());
        assertEquals("Ivanov", student.getLastName());
        assertEquals(1, student.getGroups().size());

        Assertions.assertTrue(
                student.getGroups()
                        .stream()
                        .anyMatch(g -> g.getId().equals(group.getId()))
        );
    }

    @Test
    void shouldUpdateStudent() {
        Student student = studentRepository.save(
                new Student("Ivan", "Ivanov")
        );

        StudentUpdateDTO request = new StudentUpdateDTO(
                "Petr",
                "Petrov"
        );

        ResponseEntity<StudentDTO> response =
                restTemplate.exchange(
                        "/api/v1/students/" + student.getId(),
                        HttpMethod.PUT,
                        new HttpEntity<>(request),
                        StudentDTO.class
                );

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        Student updatedStudent = studentRepository
                .findWithGroupsById(student.getId())
                .orElseThrow();

        assertEquals("Petr", updatedStudent.getFirstName());
        assertEquals("Petrov", updatedStudent.getLastName());
    }

    @Test
    void shouldDeleteStudent() {
        Student student = studentRepository.save(
                new Student("Ivan", "Ivanov")
        );

        groupRepository.save(
                new Group("New group", Set.of(student))
        );

        ResponseEntity<Void> response =
                restTemplate.exchange(
                        "/api/v1/students/" + student.getId(),
                        HttpMethod.DELETE,
                        null,
                        Void.class
                );

        assertEquals(
                HttpStatus.NO_CONTENT,
                response.getStatusCode()
        );

        Assertions.assertTrue(
                studentRepository.findById(student.getId()).isEmpty()
        );
    }

    @Test
    void shouldReturn400WhenCreateRequestIsInvalid() {
        Group group = groupRepository.save(
                new Group("New group")
        );

        StudentCreateDTO request = new StudentCreateDTO(
                "",
                "Ivanov",
                Set.of(group.getId())
        );

        ResponseEntity<ErrorResponseDTO> response =
                restTemplate.postForEntity(
                        "/api/v1/students",
                        request,
                        ErrorResponseDTO.class
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertNotNull(
                response.getBody()
                        .errors()
                        .get("firstName")
        );
    }

    @Test
    void shouldReturn404WhenCreateRequestHasNoGroups() {
        StudentCreateDTO request = new StudentCreateDTO(
                "Ivan",
                "Ivanov",
                Set.of(1L)
        );

        ResponseEntity<ErrorResponseDTO> response =
                restTemplate.postForEntity(
                        "/api/v1/students",
                        request,
                        ErrorResponseDTO.class
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                ErrorMessage.GROUPS_NOT_FOUND.getMessage(),
                response.getBody().message()
        );
    }

    @Test
    void shouldReturn400WhenUpdateRequestIsInvalid() {
        Student student = studentRepository.save(
                new Student("Ivan", "Ivanov")
        );

        StudentUpdateDTO request = new StudentUpdateDTO(
                "",
                "Petrov"
        );

        ResponseEntity<ErrorResponseDTO> response =
                restTemplate.exchange(
                        "/api/v1/students/" + student.getId(),
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
                response.getBody()
                        .errors()
                        .get("firstName")
        );
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentStudent() {
        ResponseEntity<ErrorResponseDTO> response =
                restTemplate.exchange(
                        "/api/v1/students/1111",
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
                ErrorMessage.STUDENT_NOT_FOUND.format(1111),
                response.getBody().message()
        );
    }
}