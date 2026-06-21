package com.student.mgmtsys.service.impl;

import com.student.mgmtsys.dto.CourseDto;
import com.student.mgmtsys.dto.StudentDto;
import com.student.mgmtsys.dto.StudentUpdateProfileDto;
import com.student.mgmtsys.entity.Student;
import com.student.mgmtsys.exception.StudentNotFoundException;
import com.student.mgmtsys.mapper.CourseMapper;
import com.student.mgmtsys.mapper.StudentMapper;
import com.student.mgmtsys.repository.StudentRepository;
import com.student.mgmtsys.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    public StudentDto updateStudent(Long id, StudentUpdateProfileDto studentProfileDto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found"));

        if (studentProfileDto.getEmail() != null) student.setEmail(studentProfileDto.getEmail());
        if (studentProfileDto.getPhoneNumber() != null) student.setPhoneNumber(studentProfileDto.getPhoneNumber());
        if (studentProfileDto.getParentName() != null) student.setParentName(studentProfileDto.getParentName());

        if (studentProfileDto.getAddress() != null) {
            student.getAddresses().stream()
                    .filter(address -> address.getType().equals(studentProfileDto.getAddress().getType()))
                    .findFirst()
                    .ifPresent(oldAddress -> oldAddress.setAddress(studentProfileDto.getAddress().getAddress()));
        }

        Student savedStudent = studentRepository.save(student);
        return StudentMapper.toDto(savedStudent);
    }

    @Override
    public List<CourseDto> getAllCoursesByStudentId(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found"));
        return student.getEnrolledCourses().stream().map(CourseMapper::toDto).toList();
    }

    @Override
    public void unenrollStudentFromCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found"));
        student.getEnrolledCourses().removeIf(course -> course.getId().equals(courseId));
        studentRepository.save(student);
    }
}
