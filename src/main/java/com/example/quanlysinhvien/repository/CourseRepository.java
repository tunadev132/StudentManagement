package com.example.quanlysinhvien.repository;

import com.example.quanlysinhvien.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Course findByCourseName(String name);
}
