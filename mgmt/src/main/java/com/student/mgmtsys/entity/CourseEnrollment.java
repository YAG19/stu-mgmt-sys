//package com.student.mgmtsys.entity;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "course_enrollment")
//public class CourseEnrollment {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//
//    @ManyToMany(mappedBy = "enrolledCourses")
//    private Student student;
//
//    @ManyToMany
//    private Course course;
//}
