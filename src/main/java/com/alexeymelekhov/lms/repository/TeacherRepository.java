package com.alexeymelekhov.lms.repository;

import com.alexeymelekhov.lms.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
