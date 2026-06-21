package com.student.mgmtsys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "courses")
@Data
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    @Column(nullable = false)
    private String courseName;

    private String courseDescription;

    @Column(nullable = false)
    private String courseType;

    private String courseDuration;

    @ElementCollection
    @CollectionTable(name = "course_topics", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "topic")
    private List<String> topics;

    @ManyToMany(mappedBy = "enrolledCourses")
    private Set<Student> students = new HashSet<>();
}
