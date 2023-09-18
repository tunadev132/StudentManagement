package com.example.quanlysinhvien.dto;

import lombok.Data;
import java.sql.Date;

@Data
public class CourseDTO {
    private Long id;
    private String courseName;
    private String time;
    private String description;
}
