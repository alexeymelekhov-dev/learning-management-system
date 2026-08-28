package com.alexeymelekhov.lms.mapper;

import com.alexeymelekhov.lms.dto.schedule.ScheduleCreateDTO;
import com.alexeymelekhov.lms.dto.schedule.ScheduleDTO;
import com.alexeymelekhov.lms.dto.schedule.ScheduleUpdateDTO;
import com.alexeymelekhov.lms.model.Course;
import com.alexeymelekhov.lms.model.Group;
import com.alexeymelekhov.lms.model.Schedule;
import com.alexeymelekhov.lms.model.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    @Mapping(source = "group.id", target = "groupId")
    @Mapping(source = "teacher.id", target = "teacherId")
    @Mapping(source = "course.id", target = "courseId")
    ScheduleDTO toDTO(Schedule schedule);

    @Mapping(target = "id", ignore = true)
    Schedule toEntity(
            ScheduleCreateDTO dto,
            Group group,
            Course course,
            Teacher teacher
    );

    @Mapping(target = "id", ignore = true)
    void updateScheduleFromDTO(
            ScheduleUpdateDTO dto,
            @MappingTarget Schedule schedule
    );
}
