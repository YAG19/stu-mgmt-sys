package com.student.mgmtsys.controller;

import com.student.mgmtsys.dto.ApiResponse;
import com.student.mgmtsys.dto.CourseDto;
import com.student.mgmtsys.dto.StudentDto;
import com.student.mgmtsys.dto.StudentUpdateProfileDto;
import com.student.mgmtsys.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@AllArgsConstructor
public class StudentController {

    private StudentService studentService;

    @PutMapping("/{id}")
    @PreAuthorize("@studentSecurity.isSelf(#id, authentication)")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable Long id, @RequestBody StudentUpdateProfileDto studentProfileDto) {
        StudentDto updatedStudent = studentService.updateStudent(id, studentProfileDto);
        return ResponseEntity.ok(updatedStudent);
    }


    @GetMapping("/{studentId}/courses")
    @PreAuthorize("@studentSecurity.isSelf(#studentId, authentication)")
    public ResponseEntity<List<CourseDto>> getAllCourseByStudentId(@PathVariable Long studentId) {
        List<CourseDto> courses = studentService.getAllCoursesByStudentId(studentId);
        return ResponseEntity.ok(courses);
    }

    @DeleteMapping("/{studentId}/course/{courseId}")
    @PreAuthorize("@studentSecurity.isSelf(#studentId, authentication)")
    public ResponseEntity<ApiResponse> unenrollStudentFromCourse(@PathVariable Long studentId, @PathVariable Long courseId) {
        studentService.unenrollStudentFromCourse(studentId, courseId);
        return ResponseEntity.ok(new ApiResponse("Success", "Student unenrolled from course successfully"));
    }

}
