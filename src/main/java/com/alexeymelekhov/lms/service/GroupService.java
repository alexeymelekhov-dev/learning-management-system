package com.alexeymelekhov.lms.service;

import com.alexeymelekhov.lms.dto.group.GroupCreateDTO;
import com.alexeymelekhov.lms.dto.group.GroupDTO;
import com.alexeymelekhov.lms.dto.group.GroupUpdateDTO;
import com.alexeymelekhov.lms.dto.student.StudentIdsDTO;
import com.alexeymelekhov.lms.exception.ErrorMessage;
import com.alexeymelekhov.lms.exception.ResourceNotFoundException;
import com.alexeymelekhov.lms.mapper.GroupMapper;
import com.alexeymelekhov.lms.model.Group;
import com.alexeymelekhov.lms.model.Student;
import com.alexeymelekhov.lms.repository.GroupRepository;
import com.alexeymelekhov.lms.repository.ScheduleRepository;
import com.alexeymelekhov.lms.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final ScheduleRepository scheduleRepository;

    private final GroupMapper groupMapper;

    public GroupDTO createGroup(GroupCreateDTO dto) {
        Group group = groupMapper.toEntity(dto);

        Group savedGroup = groupRepository.save(group);

        return groupMapper.toDTO(savedGroup);
    }

    public GroupDTO updateGroup(Long id, GroupUpdateDTO dto) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessage.GROUPS_NOT_FOUND.format(id)
                ));

        groupMapper.updateGroupFromDTO(dto, group);

        Group updatedGroup = groupRepository.save(group);

        return groupMapper.toDTO(updatedGroup);
    }

    @Transactional
    public void deleteGroup(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessage.GROUPS_NOT_FOUND.format(id)
                ));

        groupRepository.deleteCourseRelations(group.getId());
        groupRepository.deleteStudentRelations(group.getId());
        scheduleRepository.deleteByGroupId(id);

        groupRepository.delete(group);
    }

    public void addStudentsToGroup(Long id, StudentIdsDTO dto) {
        Group group = groupRepository.findGroupWithStudentsById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessage.GROUPS_NOT_FOUND.format(id)
                ));

        List<Student> students = studentRepository.findAllById(dto.studentIds());

        if (dto.studentIds().size() != students.size()) {
            throw new ResourceNotFoundException(
                    ErrorMessage.STUDENTS_NOT_FOUND.getMessage()
            );
        }

        group.getStudents().addAll(students);

        groupRepository.save(group);
    }
}
