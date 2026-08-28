package com.alexeymelekhov.lms.repository;

import com.alexeymelekhov.lms.model.Course;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @EntityGraph(attributePaths = "groups")
    Optional<Course> findWithGroupsById(Long id);

    @Modifying
    @Query(value = "DELETE FROM course_groups WHERE course_id = :id", nativeQuery = true)
    void deleteGroupRelations(Long id);
}
