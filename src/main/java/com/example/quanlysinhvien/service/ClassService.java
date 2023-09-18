package com.example.quanlysinhvien.service;

import com.example.quanlysinhvien.dto.ClassCourseDTO;
import com.example.quanlysinhvien.dto.ClassDTO;
import com.example.quanlysinhvien.dto.StudentClassDTO;
import com.example.quanlysinhvien.model.ClassM;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClassService {
    List<ClassM> listAll();
    void save(ClassM classes);
    ClassM get(Long id);
    void delete(Long id) throws ChangeSetPersister.NotFoundException;
    List<ClassCourseDTO> findClass();
    List<StudentClassDTO> listClassOfStudent(Long id);
    List<ClassCourseDTO> listClassNotRegist(Long id);
    void deleteClassRegistered(Long studentId, Long classId);
}
