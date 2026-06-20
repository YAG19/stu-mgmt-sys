package com.student.mgmt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "students")
@Getter
@Setter
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Date dob;
    private String gender;
    private String code;

//    Address address;

}