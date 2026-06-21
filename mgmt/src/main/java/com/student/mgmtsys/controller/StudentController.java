package com.student.mgmtsys.controller;

import com.student.mgmtsys.dto.CourseDto;
import com.student.mgmtsys.dto.StudentDto;
import com.student.mgmtsys.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@AllArgsConstructor
public class StudentController {

    private StudentService studentService;

    @PutMapping("/{id}")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable Long id, @RequestBody StudentDto studentDto) {
        StudentDto updatedStudent = studentService.updateStudent(id, studentDto);
        return ResponseEntity.ok(updatedStudent);
    }


    @GetMapping("/{id}/courses")
    public ResponseEntity<List<CourseDto>> getAllCourseByStudentId(@PathVariable Long studentId) {
        List<CourseDto> courses = studentService.getAllCoursesByStudentId(studentId);
        return ResponseEntity.ok(courses);
    }

    @DeleteMapping("/{studentId}/course/{courseId}")
    public ResponseEntity<Void> unenrollStudentFromCourse(@PathVariable Long studentId, @PathVariable Long courseId) {
        studentService.unenrollStudentFromCourse(studentId, courseId);
        return ResponseEntity.noContent().build();
    }

}
