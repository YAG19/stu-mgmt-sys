package com.student.mgmtsys.service;

import com.student.mgmtsys.dto.AddressDto;
import com.student.mgmtsys.dto.CourseDto;
import com.student.mgmtsys.dto.StudentDto;
import com.student.mgmtsys.entity.Address;
import com.student.mgmtsys.entity.Course;
import com.student.mgmtsys.entity.Student;
import com.student.mgmtsys.exception.CourseNotFoundException;
import com.student.mgmtsys.exception.StudentNotFoundException;
import com.student.mgmtsys.repository.CourseRepository;
import com.student.mgmtsys.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AdminService {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public StudentDto getStudent(Long id) {
        Student studentOptional = studentRepository.findById(id).orElseThrow(RuntimeException::new);
        return mapStudentToDto(studentOptional);
    }

    public StudentDto addStudent(StudentDto studentDto) {
        Student student = mapToStudent(studentDto);
        List<Address> address = mapToAddress(studentDto.getAddresses());
        for(Address adres: address)
        {
            adres.setStudent(student);
        }
        student.setAddresses(address);
        Student savedStudent = studentRepository.save(student);
        return mapStudentToDto(savedStudent);
    }

    public static Student mapToStudent(StudentDto studentDto){
        Student student = new Student();
        student.setName(studentDto.getName());
        student.setGender(studentDto.getGender());
        student.setDob(studentDto.getDateOfBirth());
//        student.setEmail(studentDto.getEmail());
        student.setCode(studentDto.getUniqueCode());
//        student.setPhoneNumber(studentDto.getPhoneNumber());
//        student.setParentName(studentDto.getParentName());
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

    public static StudentDto mapStudentToDto(Student student){
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
//        studentDto.setEmail(student.getEmail());
//        studentDto.setParentName(student.getParentName());
//        studentDto.setPhoneNumber(student.getPhoneNumber());
        studentDto.setUniqueCode(student.getCode());
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

    public StudentDto getStudentByName(String name) {
        Student student = studentRepository.findByName(name);
        return mapStudentToDto(student);
    }

    public void enrollStudent(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new StudentNotFoundException("Student not found"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException("Course not found"));
        student.getEnrolledCourses().add(course);
        studentRepository.save(student);
    }

    public CourseDto createCourses(CourseDto courseDto) {
        Course course = mapDtoToCourse(courseDto);;
        Course savedCourse = courseRepository.save(course);
        return mapCourseToDTO(savedCourse);
    }

    public static CourseDto mapCourseToDTO(Course savedCourse) {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(savedCourse.getId());
        courseDto.setCourseName(savedCourse.getCourseName());
        courseDto.setCourseType(savedCourse.getCourseType());
        courseDto.setCourseDescription(savedCourse.getCourseDescription());
        courseDto.setCourseDuration(savedCourse.getCourseDuration());
        courseDto.setTopics(savedCourse.getTopics());
        return courseDto;
    }

    private static Course mapDtoToCourse(CourseDto courseDto) {
        Course course = new Course();
        course.setCourseName(courseDto.getCourseName());
        course.setCourseType(courseDto.getCourseType());
        course.setCourseDescription(courseDto.getCourseDescription());
        course.setCourseDuration(courseDto.getCourseDuration());
        course.setTopics(courseDto.getTopics());
        return course;
    }

    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream().map(AdminService::mapCourseToDTO).toList();
    }
}
