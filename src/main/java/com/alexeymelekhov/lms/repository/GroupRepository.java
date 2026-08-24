package com.alexeymelekhov.lms.repository;

import com.alexeymelekhov.lms.model.Group;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    @EntityGraph(attributePaths = "students")
    Optional<Group> findGroupWithStudentsById(Long id);

    @Modifying
    @Query(value = "DELETE FROM course_groups WHERE group_id = :groupId", nativeQuery = true)
    void deleteCourseRelations(Long groupId);

    @Modifying
    @Query(value = "DELETE FROM group_students WHERE group_id = :groupId", nativeQuery = true)
    void deleteStudentRelations(Long groupId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM course_groups", nativeQuery = true)
    void deleteAllCourseRelations();
}
