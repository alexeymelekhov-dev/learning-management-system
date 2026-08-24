package com.alexeymelekhov.lms.mapper;

import com.alexeymelekhov.lms.dto.teacher.TeacherCreateDTO;
import com.alexeymelekhov.lms.dto.teacher.TeacherDTO;
import com.alexeymelekhov.lms.dto.teacher.TeacherUpdateDTO;
import com.alexeymelekhov.lms.model.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    TeacherDTO toDTO(Teacher teacher);

    @Mapping(target = "id", ignore = true)
    Teacher toEntity(TeacherCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateTeacherFromDTO(
            TeacherUpdateDTO dto,
            @MappingTarget Teacher teacher
    );
}
