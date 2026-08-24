package com.alexeymelekhov.lms.mapper;

import com.alexeymelekhov.lms.dto.course.CourseCreateDTO;
import com.alexeymelekhov.lms.dto.course.CourseDTO;
import com.alexeymelekhov.lms.dto.course.CourseUpdateDTO;
import com.alexeymelekhov.lms.model.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseDTO toDTO(Course course);

    @Mapping(target = "id", ignore = true)
    Course toEntity(CourseCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateCourseFromDTO(
            CourseUpdateDTO dto,
            @MappingTarget Course course
    );
}
