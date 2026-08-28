package com.alexeymelekhov.lms.service;

import com.alexeymelekhov.lms.dto.course.CourseCreateDTO;
import com.alexeymelekhov.lms.dto.course.CourseDTO;
import com.alexeymelekhov.lms.dto.course.CourseUpdateDTO;
import com.alexeymelekhov.lms.dto.group.GroupIdsDTO;
import com.alexeymelekhov.lms.exception.ErrorMessage;
import com.alexeymelekhov.lms.exception.ResourceNotFoundException;
import com.alexeymelekhov.lms.mapper.CourseMapper;
import com.alexeymelekhov.lms.model.Course;
import com.alexeymelekhov.lms.model.Group;
import com.alexeymelekhov.lms.repository.CourseRepository;
import com.alexeymelekhov.lms.repository.GroupRepository;
import com.alexeymelekhov.lms.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final GroupRepository groupRepository;
    private final ScheduleRepository scheduleRepository;

    public CourseDTO createCourse(CourseCreateDTO dto) {
        Course course = courseMapper.toEntity(dto);

        Course savedCourse = courseRepository.save(course);

        return courseMapper.toDTO(savedCourse);
    }

    public CourseDTO updateCourse(Long id, CourseUpdateDTO dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessage.COURSE_NOT_FOUND.format(id)
                ));

        courseMapper.updateCourseFromDTO(dto, course);

        Course updatedCourse = courseRepository.save(course);

        return courseMapper.toDTO(updatedCourse);
    }

    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessage.COURSE_NOT_FOUND.format(id)
                ));

        courseRepository.deleteGroupRelations(course.getId());
        scheduleRepository.deleteByCourseId(id);

        courseRepository.deleteById(course.getId());
    }

    public void addGroupsToCourse(Long courseId, GroupIdsDTO dto) {
        Course course = courseRepository.findWithGroupsById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessage.COURSE_NOT_FOUND.format(courseId)
                ));

        List<Group> groups = groupRepository.findAllById(dto.groupIds());

        if (dto.groupIds().size() != groups.size()) {
            throw new ResourceNotFoundException(ErrorMessage.GROUPS_NOT_FOUND.getMessage());
        }

        System.out.println("LALALA");

        course.getGroups().addAll(groups);

        courseRepository.save(course);
    }
}
