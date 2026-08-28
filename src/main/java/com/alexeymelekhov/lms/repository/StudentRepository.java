package com.alexeymelekhov.lms.repository;

import com.alexeymelekhov.lms.model.Student;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @EntityGraph(attributePaths = "groups")
    Optional<Student> findWithGroupsById(Long id);
}
