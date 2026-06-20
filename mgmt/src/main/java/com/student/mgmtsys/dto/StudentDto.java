package com.student.mgmtsys.dto;


import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class StudentDto {

    @Nonnull
    private String name;
    @Nonnull
    private Date dateOfBirth;
    @Nonnull
    private String gender;
    private Long id;
//    private AddressDto address;
}