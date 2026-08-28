package com.alexeymelekhov.lms.service;

import com.alexeymelekhov.lms.dto.common.PaginatedResponseDTO;
import com.alexeymelekhov.lms.dto.schedule.ScheduleCreateDTO;
import com.alexeymelekhov.lms.dto.schedule.ScheduleDTO;
import com.alexeymelekhov.lms.dto.schedule.ScheduleUpdateDTO;
import com.alexeymelekhov.lms.exception.ErrorMessage;
import com.alexeymelekhov.lms.exception.ResourceNotFoundException;
import com.alexeymelekhov.lms.mapper.ScheduleMapper;
import com.alexeymelekhov.lms.model.Course;
import com.alexeymelekhov.lms.model.Group;
import com.alexeymelekhov.lms.model.Schedule;
import com.alexeymelekhov.lms.model.Teacher;
import com.alexeymelekhov.lms.repository.CourseRepository;
import com.alexeymelekhov.lms.repository.GroupRepository;
import com.alexeymelekhov.lms.repository.ScheduleRepository;
import com.alexeymelekhov.lms.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final GroupRepository groupRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final ScheduleMapper scheduleMapper;

    public ScheduleDTO createSchedule(Long groupId, ScheduleCreateDTO dto) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessage.GROUPS_NOT_FOUND.format(groupId)
                ));

        Course course = courseRepository.findById(dto.courseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessage.COURSE_NOT_FOUND.format(dto.courseId())
                ));

        Teacher teacher = teacherRepository.findById(dto.teacherId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessage.TEACHER_NOT_FOUND.format(dto.teacherId())
                ));

        Schedule schedule = scheduleMapper.toEntity(
                dto,
                group,
                course,
                teacher
        );

        Schedule savedSchedule = scheduleRepository.save(schedule);

        return scheduleMapper.toDTO(savedSchedule);
    }

    public PaginatedResponseDTO<ScheduleDTO> getSchedulesByGroupId(
            Long id,
            Pageable pageable
    ) {
        if (!groupRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    ErrorMessage.GROUPS_NOT_FOUND.format(id)
            );
        }

        Page<Schedule> schedules =
                scheduleRepository.findAllByGroupId(id, pageable);

        return new PaginatedResponseDTO<>(
                schedules.map(scheduleMapper::toDTO).getContent(),
                schedules.getTotalElements(),
                schedules.getTotalPages(),
                schedules.getNumber(),
                schedules.getSize()
        );
    }

    public PaginatedResponseDTO<ScheduleDTO> getSchedulesByTeacherId(
            Long id,
            Pageable pageable
    ) {
        if (!teacherRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    ErrorMessage.TEACHER_NOT_FOUND.format(id)
            );
        }

        Page<Schedule> schedules =
                scheduleRepository.findAllByTeacherId(id, pageable);

        return new PaginatedResponseDTO<>(
                schedules.map(scheduleMapper::toDTO).getContent(),
                schedules.getTotalElements(),
                schedules.getTotalPages(),
                schedules.getNumber(),
                schedules.getSize()
        );
    }

    public ScheduleDTO updateSchedule(
            Long groupId,
            Long scheduleId,
            ScheduleUpdateDTO dto
    ) {
        Schedule schedule = scheduleRepository.findByIdAndGroupId(
                        scheduleId,
                        groupId
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessage.SCHEDULE_NOT_FOUND.format(scheduleId)
                ));

        scheduleMapper.updateScheduleFromDTO(dto, schedule);

        Schedule updatedSchedule = scheduleRepository.save(schedule);

        return scheduleMapper.toDTO(updatedSchedule);
    }

    public void deleteSchedule(Long groupId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findByIdAndGroupId(
                        scheduleId,
                        groupId
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessage.SCHEDULE_NOT_FOUND.format(scheduleId)
                ));

        scheduleRepository.deleteById(schedule.getId());
    }
}
