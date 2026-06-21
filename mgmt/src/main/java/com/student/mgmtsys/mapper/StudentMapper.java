package com.student.mgmtsys.mapper;

import com.student.mgmtsys.dto.AddressDto;
import com.student.mgmtsys.dto.StudentDto;
import com.student.mgmtsys.entity.Address;
import com.student.mgmtsys.entity.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps between {@link Student}/{@link Address} entities and their DTOs.
 */
public final class StudentMapper {

    private StudentMapper() {
    }

    public static Student toEntity(StudentDto studentDto) {
        Student student = new Student();
        student.setName(studentDto.getName());
        student.setGender(studentDto.getGender());
        student.setDateOfBirth(studentDto.getDateOfBirth());
        student.setCode(studentDto.getUniqueCode());
        return student;
    }

    public static List<Address> toAddressEntities(List<AddressDto> addressDto) {
        List<Address> addresses = new ArrayList<>();
        if (addressDto == null || addressDto.isEmpty()) {
            return addresses;
        }
        for (AddressDto addr : addressDto) {
            Address address = new Address();
            address.setType(addr.getType());
            address.setAddress(addr.getAddress());
            addresses.add(address);
        }
        return addresses;
    }

    public static StudentDto toDto(Student student) {
        if (student == null) {
            return null;
        }

        StudentDto studentDto = new StudentDto();

        if (student.getAddresses() != null && !student.getAddresses().isEmpty()) {
            studentDto.setAddresses(toAddressDtos(student.getAddresses()));
        }

        studentDto.setName(student.getName());
        studentDto.setId(student.getId());
        studentDto.setDateOfBirth(student.getDateOfBirth());
        studentDto.setGender(student.getGender());
        studentDto.setUniqueCode(student.getCode());
        return studentDto;
    }

    private static List<AddressDto> toAddressDtos(List<Address> addresses) {
        List<AddressDto> addressDto = new ArrayList<>();
        for (Address address : addresses) {
            AddressDto dto = new AddressDto();
            dto.setType(address.getType());
            dto.setAddress(address.getAddress());
            addressDto.add(dto);
        }
        return addressDto;
    }
}
