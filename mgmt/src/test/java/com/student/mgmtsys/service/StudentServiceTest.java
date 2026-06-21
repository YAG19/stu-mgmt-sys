package com.student.mgmtsys.service;

import com.student.mgmtsys.dto.AddressDto;
import com.student.mgmtsys.dto.CourseDto;
import com.student.mgmtsys.dto.StudentDto;
import com.student.mgmtsys.dto.StudentUpdateProfileDto;
import com.student.mgmtsys.entity.Address;
import com.student.mgmtsys.entity.Course;
import com.student.mgmtsys.entity.Student;
import com.student.mgmtsys.exception.StudentNotFoundException;
import com.student.mgmtsys.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    // ---------- updateStudent ----------

    @Test
    void updateStudent_updatesContactFields_andMatchingAddress() {
        Student student = buildStudent(1L, "Alice");
        Address home = buildAddress("HOME", "Old Home Address");
        Address work = buildAddress("WORK", "Old Work Address");
        student.setAddresses(new ArrayList<>(List.of(home, work)));

        StudentUpdateProfileDto dto = new StudentUpdateProfileDto();
        dto.setEmail("alice@example.com");
        dto.setPhoneNumber("123456789");
        dto.setParentName("Mr. Smith");
        AddressDto addressDto = new AddressDto();
        addressDto.setType("HOME");
        addressDto.setAddress("New Home Address");
        dto.setAddress(addressDto);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        StudentDto result = studentService.updateStudent(1L, dto);

        // only the HOME address changes, WORK stays the same
        assertThat(home.getAddress()).isEqualTo("New Home Address");
        assertThat(work.getAddress()).isEqualTo("Old Work Address");
        // contact fields are set on the entity (note: mapper does not expose them on dto)
        assertThat(student.getEmail()).isEqualTo("alice@example.com");
        assertThat(student.getPhoneNumber()).isEqualTo("123456789");
        assertThat(student.getParentName()).isEqualTo("Mr. Smith");
        verify(studentRepository).save(student);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void updateStudent_leavesAddressesUntouched_whenNoTypeMatches() {
        Student student = buildStudent(1L, "Alice");
        Address home = buildAddress("HOME", "Original Address");
        student.setAddresses(new ArrayList<>(List.of(home)));

        StudentUpdateProfileDto dto = new StudentUpdateProfileDto();
        AddressDto addressDto = new AddressDto();
        addressDto.setType("WORK"); // no WORK address exists
        addressDto.setAddress("Should Not Apply");
        dto.setAddress(addressDto);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        studentService.updateStudent(1L, dto);

        assertThat(home.getAddress()).isEqualTo("Original Address");
        verify(studentRepository).save(student);
    }

    @Test
    void updateStudent_throws_whenStudentMissing() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.updateStudent(99L, new StudentUpdateProfileDto()))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student not found");

        verify(studentRepository, never()).save(any());
    }

    // ---------- getAllCoursesByStudentId ----------

    @Test
    void getAllCoursesByStudentId_returnsMappedCourses() {
        Student student = buildStudent(1L, "Alice");
        student.setEnrolledCourses(new ArrayList<>(List.of(
                buildCourse(10L, "Math"),
                buildCourse(20L, "Science"))));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        List<CourseDto> result = studentService.getAllCoursesByStudentId(1L);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CourseDto::getCourseName)
                .containsExactly("Math", "Science");
    }

    @Test
    void getAllCoursesByStudentId_returnsEmpty_whenNoEnrollments() {
        Student student = buildStudent(1L, "Alice");
        student.setEnrolledCourses(new ArrayList<>());
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        assertThat(studentService.getAllCoursesByStudentId(1L)).isEmpty();
    }

    @Test
    void getAllCoursesByStudentId_throws_whenStudentMissing() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getAllCoursesByStudentId(99L))
                .isInstanceOf(StudentNotFoundException.class);
    }

    // ---------- unenrollStudentFromCourse ----------

    @Test
    void unenrollStudentFromCourse_removesMatchingCourse_andSaves() {
        Student student = buildStudent(1L, "Alice");
        Course math = buildCourse(10L, "Math");
        Course science = buildCourse(20L, "Science");
        student.setEnrolledCourses(new ArrayList<>(List.of(math, science)));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        studentService.unenrollStudentFromCourse(1L, 10L);

        assertThat(student.getEnrolledCourses()).containsExactly(science);
        verify(studentRepository).save(student);
    }

    @Test
    void unenrollStudentFromCourse_noChange_whenCourseNotEnrolled() {
        Student student = buildStudent(1L, "Alice");
        Course math = buildCourse(10L, "Math");
        student.setEnrolledCourses(new ArrayList<>(List.of(math)));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        studentService.unenrollStudentFromCourse(1L, 999L);

        assertThat(student.getEnrolledCourses()).containsExactly(math);
        verify(studentRepository).save(student);
    }

    @Test
    void unenrollStudentFromCourse_throws_whenStudentMissing() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.unenrollStudentFromCourse(99L, 10L))
                .isInstanceOf(StudentNotFoundException.class);

        verify(studentRepository, never()).save(any());
    }

    // ---------- helpers ----------

    private static Student buildStudent(Long id, String name) {
        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setGender("F");
        student.setDob(new Date());
        student.setCode("STU-" + id);
        return student;
    }

    private static Course buildCourse(Long id, String name) {
        Course course = new Course();
        course.setId(id);
        course.setCourseName(name);
        course.setCourseType("GENERAL");
        course.setCourseDuration("1 month");
        course.setTopics(List.of("intro"));
        return course;
    }

    private static Address buildAddress(String type, String value) {
        Address address = new Address();
        address.setType(type);
        address.setAddress(value);
        return address;
    }
}
