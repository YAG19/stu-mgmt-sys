package com.student.mgmtsys.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    private String courseName;
    private String courseDescription;
    private String courseType;
    private String courseDuration;
    private String topics;

//    @ManyToMany(mappedBy = "enrolledCourses")
//    private List<Student> enrolledStudents;
}
