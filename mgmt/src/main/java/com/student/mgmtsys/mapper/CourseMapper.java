package com.student.mgmtsys.mapper;

import com.student.mgmtsys.dto.CourseDto;
import com.student.mgmtsys.entity.Course;

/**
 * Maps between {@link Course} entities and {@link CourseDto}.
 */
public final class CourseMapper {

    private CourseMapper() {
    }

    public static Course toEntity(CourseDto courseDto) {
        Course course = new Course();
        course.setCourseName(courseDto.getCourseName());
        course.setCourseType(courseDto.getCourseType());
        course.setCourseDescription(courseDto.getCourseDescription());
        course.setCourseDuration(courseDto.getCourseDuration());
        course.setTopics(courseDto.getTopics());
        return course;
    }

    public static CourseDto toDto(Course course) {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(course.getId());
        courseDto.setCourseName(course.getCourseName());
        courseDto.setCourseType(course.getCourseType());
        courseDto.setCourseDescription(course.getCourseDescription());
        courseDto.setCourseDuration(course.getCourseDuration());
        courseDto.setTopics(course.getTopics());
        return courseDto;
    }
}
