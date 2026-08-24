package com.alexeymelekhov.lms.service;

import com.alexeymelekhov.lms.dto.student.StudentCreateDTO;
import com.alexeymelekhov.lms.dto.student.StudentDTO;
import com.alexeymelekhov.lms.dto.student.StudentUpdateDTO;
import com.alexeymelekhov.lms.exception.ErrorMessage;
import com.alexeymelekhov.lms.exception.ResourceNotFoundException;
import com.alexeymelekhov.lms.mapper.StudentMapper;
import com.alexeymelekhov.lms.model.Group;
import com.alexeymelekhov.lms.model.Student;
import com.alexeymelekhov.lms.repository.GroupRepository;
import com.alexeymelekhov.lms.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final StudentMapper studentMapper;

    @Transactional
    public StudentDTO createStudent(StudentCreateDTO dto) {
        Student student = studentMapper.toEntity(dto);

        Set<Group> groups = new HashSet<>(
                groupRepository.findAllById(dto.groupIds())
        );

        if (groups.size() != dto.groupIds().size()) {
            throw new ResourceNotFoundException(
                    ErrorMessage.GROUPS_NOT_FOUND.getMessage()
            );
        }

        Student savedStudent = studentRepository.save(student);

        groups.forEach(group -> {
            group.getStudents().add(savedStudent);
            savedStudent.getGroups().add(group);
        });

        return studentMapper.toDTO(savedStudent);
    }

    @Transactional
    public StudentDTO updateStudent(Long id, StudentUpdateDTO dto) {
        Student student = studentRepository.findWithGroupsById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessage.STUDENT_NOT_FOUND.format(id)
                ));

        studentMapper.updateStudentFromDTO(dto, student);

        return studentMapper.toDTO(student);
    }

    @Transactional
    public void deleteStudent(Long studentId) {
        Student student = studentRepository.findWithGroupsById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessage.STUDENT_NOT_FOUND.format(studentId)
                ));

        student.getGroups().forEach(group ->
                group.getStudents().remove(student)
        );

        studentRepository.delete(student);
    }
}
