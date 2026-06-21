package com.student.mgmtsys.service;

import com.student.mgmtsys.dto.CourseDto;
import com.student.mgmtsys.dto.StudentDto;
import com.student.mgmtsys.dto.StudentUpdateProfileDto;

import java.util.List;

public interface StudentService {

    StudentDto updateStudent(Long id, StudentUpdateProfileDto studentProfileDto);

    List<CourseDto> getAllCoursesByStudentId(Long studentId);

    void unenrollStudentFromCourse(Long studentId, Long courseId);
}
