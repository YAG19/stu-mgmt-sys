package com.student.mgmtsys.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "address")
@Getter
@Setter
public class Address {

    @Id
    private Long id;
    private String currentAddress;
    private String permanentAddress;
    private List<String> addressHistory;
}
