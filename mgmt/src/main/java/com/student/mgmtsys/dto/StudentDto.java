package com.student.mgmtsys.dto;


import jakarta.annotation.Nonnull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
public class StudentDto {

    private Long id;
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Student code is required")
    private String uniqueCode;

    @NotEmpty(message = "At least one address is required")
    @Valid
    private List<AddressDto> addresses;

//    private String email;
//    private String phoneNumber;
//    private String parentName;
//    private AddressDto address;
}