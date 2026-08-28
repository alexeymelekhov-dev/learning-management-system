package com.alexeymelekhov.lms.mapper;

import com.alexeymelekhov.lms.dto.student.StudentCreateDTO;
import com.alexeymelekhov.lms.dto.student.StudentDTO;
import com.alexeymelekhov.lms.dto.student.StudentUpdateDTO;
import com.alexeymelekhov.lms.model.Group;
import com.alexeymelekhov.lms.model.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(
            target = "groups",
            source = "groups",
            qualifiedByName = "groupsToIds"
    )
    StudentDTO toDTO(Student student);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groups", ignore = true)
    Student toEntity(StudentCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groups", ignore = true)
    void updateStudentFromDTO(
            StudentUpdateDTO dto,
            @MappingTarget Student student
    );

    @Named("groupsToIds")
    default Set<Long> groupsToIds(Set<Group> groups) {
        return groups == null
                ? new HashSet<>()
                : groups.stream()
                .map(Group::getId)
                .collect(Collectors.toSet());
    }
}
