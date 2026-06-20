package com.student.mgmt.service;

import com.student.mgmt.dto.AddressDto;
import com.student.mgmt.dto.StudentDto;
import com.student.mgmt.entity.Address;
import com.student.mgmt.entity.Student;
import com.student.mgmt.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
//@NoArgsCounstructor
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository){
        this.studentRepository = studentRepository;
    }

    public StudentDto getStudent(Long id) {
        Student studentOptional = studentRepository.findById(id).orElseThrow(RuntimeException::new);
        return studentToDto(studentOptional);
    }

    public static StudentDto studentToDto(Student student){
        if(student == null) return null;

        StudentDto studentDto = new StudentDto();

//        if(student.getAddress() != null)
//        {
//              studentDto.setAddress(mapToAddressDto(student.getAddress()));
//        }

        studentDto.setName(student.getName());
        studentDto.setId(student.getId());
        studentDto.setDateOfBirth(student.getDob());
        studentDto.setGender(student.getGender());
        return studentDto;

    }

    private static AddressDto mapToAddressDto(Address address) {
        AddressDto addressDto  = new AddressDto();
        addressDto.setCurrentAddress(address.getCurrentAddress());
        addressDto.setPermanentAddress(address.getPermanentAddress());
        return addressDto;
    }

    public StudentDto addStudent(StudentDto studentDto) {

        return studentToDto(studentRepository.save(mapToStudent(studentDto)));
    }

    public static Student mapToStudent(StudentDto studentDto){
        Student student = new Student();
        student.setName(studentDto.getName());
        student.setGender(studentDto.getGender());
        student.setDob(studentDto.getDateOfBirth());
        return student;
    }
}
