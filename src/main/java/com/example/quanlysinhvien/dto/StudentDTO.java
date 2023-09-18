package com.example.quanlysinhvien.dto;

import lombok.Data;
import java.sql.Date;

@Data
public class StudentDTO {
    private Long id;
    private String sid;
    private String name;
    private boolean gender;
    private String birthday;
    private String phone;
    private String email;
    private boolean isDeleted;
}
