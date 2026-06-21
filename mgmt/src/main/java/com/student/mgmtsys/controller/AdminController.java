package com.student.mgmtsys.controller;

import com.student.mgmtsys.dto.ApiResponse;
import com.student.mgmtsys.dto.CourseDto;
import com.student.mgmtsys.dto.StudentDto;
import com.student.mgmtsys.service.AdminService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/admin")
@Slf4j
@AllArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/student")
    public ResponseEntity<StudentDto> addStudent(@RequestBody StudentDto studentDto){
        log.info("Request for Creating Student Record for {} ", studentDto.getName());
        StudentDto savedStudent = adminService.addStudent(studentDto);
        return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
    }

    @GetMapping("/student/{id}")
    public ResponseEntity<StudentDto> getStudent(@PathVariable Long id){
        return ResponseEntity.ok(adminService.getStudent(id));
    }

    @GetMapping("/student/find")
    public ResponseEntity<StudentDto> getStudentByName(@RequestParam(name = "name") String name){
        return ResponseEntity.ok(adminService.getStudentByName(name));
    }

    @PostMapping("/courses")
    public ResponseEntity<CourseDto> createCourses(@RequestBody CourseDto courseDto){
        CourseDto addedCourse = adminService.createCourses(courseDto);
        return new ResponseEntity<>(addedCourse, HttpStatus.CREATED);
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseDto>> getAllCourses(){
        List<CourseDto> courses = adminService.getAllCourses();
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @PostMapping("/student/{studentId}/course/{courseId}/enroll")
    public ResponseEntity<ApiResponse> enrollStudent(@PathVariable Long studentId, @PathVariable Long courseId){
        adminService.enrollStudent(studentId, courseId);
        return ResponseEntity.ok(new ApiResponse("Success", "Student enrolled in course successfully"));
    }

}