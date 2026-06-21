package com.student.mgmtsys.service.impl;

import com.student.mgmtsys.dto.CourseDto;
import com.student.mgmtsys.dto.StudentDto;
import com.student.mgmtsys.entity.Address;
import com.student.mgmtsys.entity.Course;
import com.student.mgmtsys.entity.Student;
import com.student.mgmtsys.exception.CourseNotFoundException;
import com.student.mgmtsys.exception.StudentNotFoundException;
import com.student.mgmtsys.mapper.CourseMapper;
import com.student.mgmtsys.mapper.StudentMapper;
import com.student.mgmtsys.repository.CourseRepository;
import com.student.mgmtsys.repository.StudentRepository;
import com.student.mgmtsys.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Override
    public StudentDto getStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(RuntimeException::new);
        return StudentMapper.toDto(student);
    }

    @Override
    public StudentDto addStudent(StudentDto studentDto) {
        Student student = StudentMapper.toEntity(studentDto);
        List<Address> addresses = StudentMapper.toAddressEntities(studentDto.getAddresses());
        for (Address address : addresses) {
            address.setStudent(student);
        }
        student.setAddresses(addresses);
        Student savedStudent = studentRepository.save(student);
        return StudentMapper.toDto(savedStudent);
    }

    @Override
    public StudentDto getStudentByName(String name) {
        Student student = studentRepository.findByName(name);
        return StudentMapper.toDto(student);
    }

    @Override
    public void enrollStudent(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));
        student.getEnrolledCourses().add(course);
        studentRepository.save(student);
    }

    @Override
    public CourseDto createCourses(CourseDto courseDto) {
        Course course = CourseMapper.toEntity(courseDto);
        Course savedCourse = courseRepository.save(course);
        return CourseMapper.toDto(savedCourse);
    }

    @Override
    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream().map(CourseMapper::toDto).toList();
    }
}
