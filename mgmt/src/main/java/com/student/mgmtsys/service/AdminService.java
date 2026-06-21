package com.student.mgmtsys.service;

import com.student.mgmtsys.dto.CourseDto;
import com.student.mgmtsys.dto.StudentDto;

import java.util.List;

public interface AdminService {

    StudentDto getStudent(Long id);

    StudentDto addStudent(StudentDto studentDto);

    List<StudentDto> getStudentByName(String name);

    void enrollStudent(Long studentId, Long courseId);

    CourseDto createCourses(CourseDto courseDto);

    List<CourseDto> getAllCourses();
}
