package com.student.mgmtsys.service;

import com.student.mgmtsys.dto.AddressDto;
import com.student.mgmtsys.dto.StudentDto;
import com.student.mgmtsys.entity.Address;
import com.student.mgmtsys.entity.Student;
import com.student.mgmtsys.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

//    @Transactional(value = Transactional.TxType.REQUIRED)
    public StudentDto addStudent(StudentDto studentDto) {
        Student student = mapToStudent(studentDto);
        List<Address> address = mapToAddress(studentDto.getAddresses());
        for(Address adres: address)
        {
            adres.setStudent(student);
        }
        student.setAddresses(address);
        Student savedStudent = studentRepository.save(student);
        return studentToDto(savedStudent);
    }

    public static Student mapToStudent(StudentDto studentDto){
        Student student = new Student();
        student.setName(studentDto.getName());
        student.setGender(studentDto.getGender());
        student.setDob(studentDto.getDateOfBirth());
        return student;
    }

    private static List<Address> mapToAddress(List<AddressDto> addressDto) {
        List<Address> addresses = new ArrayList<>();
        if(addressDto == null || addressDto.isEmpty()) return addresses;
        for(AddressDto addr : addressDto){
            Address address = new Address();
            address.setType(addr.getType());
            address.setAddress(addr.getAddress());
            addresses.add(address);
        }
        return addresses;
    }

    public static StudentDto studentToDto(Student student){
        if(student == null) return null;

        StudentDto studentDto = new StudentDto();

        if(student.getAddresses() != null && !student.getAddresses().isEmpty())
        {
            studentDto.setAddresses(mapToAddressDto(student.getAddresses()));
        }

        studentDto.setName(student.getName());
        studentDto.setId(student.getId());
        studentDto.setDateOfBirth(student.getDob());
        studentDto.setGender(student.getGender());
        return studentDto;

    }

    private static List<AddressDto> mapToAddressDto(List<Address> addresses) {
        List<AddressDto> addressDto = new ArrayList<>();
        for(Address address : addresses){
            AddressDto dto = new AddressDto();
            dto.setType(address.getType());
            dto.setAddress(address.getAddress());
            addressDto.add(dto);
        }
        return addressDto;
    }
}
