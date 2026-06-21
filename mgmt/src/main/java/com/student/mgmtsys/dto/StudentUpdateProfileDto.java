package com.student.mgmtsys.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
public class StudentUpdateProfileDto {

    private String email;
    private String phoneNumber;
    private String parentName;
    private AddressDto address;
}
