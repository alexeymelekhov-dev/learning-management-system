package com.alexeymelekhov.lms.service;

import com.alexeymelekhov.lms.dto.teacher.TeacherCreateDTO;
import com.alexeymelekhov.lms.dto.teacher.TeacherDTO;
import com.alexeymelekhov.lms.dto.teacher.TeacherUpdateDTO;
import com.alexeymelekhov.lms.exception.ErrorMessage;
import com.alexeymelekhov.lms.exception.ResourceNotFoundException;
import com.alexeymelekhov.lms.mapper.TeacherMapper;
import com.alexeymelekhov.lms.model.Teacher;
import com.alexeymelekhov.lms.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;

    public TeacherDTO createTeacher(TeacherCreateDTO dto) {
        Teacher teacher = teacherMapper.toEntity(dto);

        Teacher savedTeacher = teacherRepository.save(teacher);

        return teacherMapper.toDTO(savedTeacher);
    }

    public TeacherDTO updateTeacher(Long id, TeacherUpdateDTO dto) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessage.TEACHER_NOT_FOUND.format(id)
                ));

        teacherMapper.updateTeacherFromDTO(dto, teacher);

        Teacher updatedTeacher = teacherRepository.save(teacher);

        return teacherMapper.toDTO(updatedTeacher);
    }

    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessage.TEACHER_NOT_FOUND.format(id)
                ));

        teacherRepository.deleteById(teacher.getId());
    }
}
