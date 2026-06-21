package com.student.mgmtsys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
public class CourseDto {
    private Long id;
    @NotBlank(message = "Course name is required")
    private String courseName;

    private String courseDescription;

    @NotNull(message = "Course type is required")
    private String courseType;

    private String courseDuration;

    private List<String> topics;
}
