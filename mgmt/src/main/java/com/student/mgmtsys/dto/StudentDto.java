package com.student.mgmtsys.dto;


import jakarta.annotation.Nonnull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
public class StudentDto {

    @Nonnull
    private String name;
    @Nonnull
    private Date dateOfBirth;
    @Nonnull
    private String gender;
    private Long id;
    private String uniqueCode;
    private List<AddressDto> addresses;

    private String email;
    private String phoneNumber;
    private String parentName;
    private AddressDto address;
}