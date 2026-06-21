package com.student.mgmtsys.service;

import com.student.mgmtsys.dto.CourseDto;
import com.student.mgmtsys.dto.StudentDto;
import com.student.mgmtsys.entity.Student;
import com.student.mgmtsys.exception.StudentNotFoundException;
import com.student.mgmtsys.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
@AllArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentDto updateStudent(Long id, StudentDto studentDto) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException("Student not found"));
        student.setEmail(studentDto.getEmail());
        student.setPhoneNumber(studentDto.getPhoneNumber());
        student.setParentName(studentDto.getParentName());

        student.getAddresses().stream()
                .filter( address -> address.getType().equals(studentDto.getAddress().getType()))
                .findFirst()
                .ifPresent(olddAddress -> olddAddress.setAddress(studentDto.getAddress().getAddress()));

        studentRepository.save(student);
        return AdminService.studentToDto(student);
    }


    public List<CourseDto> getAllCoursesByStudentId(Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new StudentNotFoundException("Student not found"));
        return student.getEnrolledCourses().stream().map(AdminService::mapCourseToDTO).toList();
    }

    public void unenrollStudentFromCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new StudentNotFoundException("Student not found"));
        student.getEnrolledCourses().removeIf(course -> course.getId().equals(courseId));
        studentRepository.save(student);
    }
}
