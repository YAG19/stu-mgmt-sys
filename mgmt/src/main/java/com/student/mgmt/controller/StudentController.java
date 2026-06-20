package com.student.mgmt.controller;

import com.student.mgmt.dto.StudentDto;
import com.student.mgmt.service.StudentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/v1")
@Slf4j
@AllArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/student")
    public ResponseEntity<StudentDto> addStudent(@RequestBody StudentDto studentDto){
        log.info("Request for Creating Student Record for {} ", studentDto.getName());
        StudentDto savedStudent = studentService.addStudent(studentDto);
        return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
    }


    @GetMapping("/student/{id}")
    public ResponseEntity<StudentDto> getStudent(@PathVariable Long id){
        return ResponseEntity.ok(studentService.getStudent(id));
    }

}