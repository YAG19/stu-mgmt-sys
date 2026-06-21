package com.student.mgmtsys.service;

import com.student.mgmtsys.dto.AddressDto;
import com.student.mgmtsys.dto.CourseDto;
import com.student.mgmtsys.dto.StudentDto;
import com.student.mgmtsys.entity.Address;
import com.student.mgmtsys.entity.Course;
import com.student.mgmtsys.entity.Student;
import com.student.mgmtsys.exception.CourseNotFoundException;
import com.student.mgmtsys.exception.StudentNotFoundException;
import com.student.mgmtsys.mapper.StudentMapper;
import com.student.mgmtsys.repository.CourseRepository;
import com.student.mgmtsys.repository.StudentRepository;
import com.student.mgmtsys.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    // ---------- getStudent ----------

    @Test
    void getStudent_returnsMappedDto_whenStudentExists() {
        Student student = buildStudent(1L, "Alice");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        StudentDto result = adminService.getStudent(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Alice");
        assertThat(result.getGender()).isEqualTo("F");
        assertThat(result.getUniqueCode()).isEqualTo("STU-1");
    }

    @Test
    void getStudent_throws_whenStudentMissing() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.getStudent(99L))
                .isInstanceOf(RuntimeException.class);
    }

    // ---------- addStudent ----------

    @Test
    void addStudent_persistsStudent_andLinksAddressesBackToStudent() {
        StudentDto dto = new StudentDto();
        dto.setName("Bob");
        dto.setGender("M");
        dto.setDateOfBirth(LocalDate.of(2000, 1, 1));
        dto.setUniqueCode("STU-2");
        AddressDto addressDto = new AddressDto();
        addressDto.setType("HOME");
        addressDto.setAddress("221B Baker Street");
        dto.setAddresses(List.of(addressDto));

        // return whatever is saved so the mapping back to dto can be asserted
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        StudentDto result = adminService.addStudent(dto);

        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(captor.capture());
        Student saved = captor.getValue();

        assertThat(saved.getName()).isEqualTo("Bob");
        assertThat(saved.getCode()).isEqualTo("STU-2");
        assertThat(saved.getAddresses()).hasSize(1);
        // each address must point back to the owning student
        assertThat(saved.getAddresses().get(0).getStudent()).isSameAs(saved);
        assertThat(saved.getAddresses().get(0).getType()).isEqualTo("HOME");

        assertThat(result.getName()).isEqualTo("Bob");
        assertThat(result.getAddresses()).hasSize(1);
        assertThat(result.getAddresses().get(0).getAddress()).isEqualTo("221B Baker Street");
    }

    @Test
    void addStudent_handlesNullAddresses() {
        StudentDto dto = new StudentDto();
        dto.setName("Carol");
        dto.setGender("F");
        dto.setDateOfBirth(LocalDate.of(2000, 1, 1));
        dto.setAddresses(null);
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        StudentDto result = adminService.addStudent(dto);

        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(captor.capture());
        assertThat(captor.getValue().getAddresses()).isEmpty();
        assertThat(result.getAddresses()).isNull();
    }

    // ---------- getStudentByName ----------

    @Test
    void getStudentByName_returnsMappedDto() {
        Student student = buildStudent(5L, "Dave");
        when(studentRepository.findByName("Dave")).thenReturn(student);

        StudentDto result = adminService.getStudentByName("Dave");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Dave");
    }

    @Test
    void getStudentByName_returnsNull_whenNotFound() {
        when(studentRepository.findByName("Ghost")).thenReturn(null);

        assertThat(adminService.getStudentByName("Ghost")).isNull();
    }

    // ---------- enrollStudent ----------

    @Test
    void enrollStudent_addsCourse_andSaves() {
        Student student = buildStudent(1L, "Alice");
        Course course = buildCourse(10L, "Math");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));

        adminService.enrollStudent(1L, 10L);

        assertThat(student.getEnrolledCourses()).containsExactly(course);
        verify(studentRepository).save(student);
    }

    @Test
    void enrollStudent_throws_whenStudentMissing() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.enrollStudent(1L, 10L))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student not found");

        verify(courseRepository, never()).findById(any());
        verify(studentRepository, never()).save(any());
    }

    @Test
    void enrollStudent_throws_whenCourseMissing() {
        Student student = buildStudent(1L, "Alice");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.enrollStudent(1L, 10L))
                .isInstanceOf(CourseNotFoundException.class)
                .hasMessageContaining("Course not found");

        verify(studentRepository, never()).save(any());
    }

    // ---------- createCourses ----------

    @Test
    void createCourses_savesAndReturnsMappedDto() {
        CourseDto dto = new CourseDto();
        dto.setCourseName("Physics");
        dto.setCourseType("SCIENCE");
        dto.setCourseDescription("Intro to physics");
        dto.setCourseDuration("3 months");
        dto.setTopics(List.of("Mechanics", "Optics"));

        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> {
            Course c = inv.getArgument(0);
            c.setId(100L);
            return c;
        });

        CourseDto result = adminService.createCourses(dto);

        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        verify(courseRepository).save(captor.capture());
        assertThat(captor.getValue().getCourseName()).isEqualTo("Physics");
        assertThat(captor.getValue().getTopics()).containsExactly("Mechanics", "Optics");

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getCourseName()).isEqualTo("Physics");
        assertThat(result.getCourseDuration()).isEqualTo("3 months");
    }

    // ---------- getAllCourses ----------

    @Test
    void getAllCourses_mapsEveryCourse() {
        when(courseRepository.findAll()).thenReturn(List.of(
                buildCourse(1L, "Math"),
                buildCourse(2L, "Science")));

        List<CourseDto> result = adminService.getAllCourses();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CourseDto::getCourseName)
                .containsExactly("Math", "Science");
    }

    @Test
    void getAllCourses_returnsEmptyList_whenNoCourses() {
        when(courseRepository.findAll()).thenReturn(List.of());

        assertThat(adminService.getAllCourses()).isEmpty();
        verify(courseRepository, times(1)).findAll();
    }

    // ---------- mapper edge cases ----------

    @Test
    void mapStudentToDto_returnsNull_forNullStudent() {
        assertThat(StudentMapper.toDto(null)).isNull();
    }

    // ---------- helpers ----------

    private static Student buildStudent(Long id, String name) {
        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setGender("F");
        student.setDateOfBirth(LocalDate.of(2000, 1, 1));
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
}
