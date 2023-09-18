package com.example.quanlysinhvien.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassCourseDTO {
    private Long id;
    private String className;
    private String courseName;
}
