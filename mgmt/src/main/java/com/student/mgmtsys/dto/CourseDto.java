package com.student.mgmtsys.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
public class CourseDto {
    private String courseName;
    private String courseDescription;
    private String courseType;
    private String courseDuration;
    private String topics;
}
