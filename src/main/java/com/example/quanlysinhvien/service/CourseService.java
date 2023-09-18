package com.example.quanlysinhvien.service;

import com.example.quanlysinhvien.dto.CourseDTO;
import com.example.quanlysinhvien.model.Course;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CourseService {
    List<CourseDTO> listAll();
    void save(CourseDTO course);
    Course createCourse(CourseDTO courseDTO);
    Course get(Long id);
    Course findByCourseName(String name);
    void delete(Long id) throws ChangeSetPersister.NotFoundException;
}
