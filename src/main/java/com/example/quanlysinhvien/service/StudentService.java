package com.example.quanlysinhvien.service;

import com.example.quanlysinhvien.dto.StudentDTO;
import com.example.quanlysinhvien.exception.UserNotFoundException;
import com.example.quanlysinhvien.model.Student;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StudentService {
    List<StudentDTO> listAll();
    Student getStudentById(Long id) throws UserNotFoundException;
    Student getStudentBySid(String sid) throws UserNotFoundException;
    Student createStudent(StudentDTO studentDto);
    void save(StudentDTO studentDTO);
    void softDeleteStudent(String sid) throws UserNotFoundException;
    List<Student> search(String keyword);
}
