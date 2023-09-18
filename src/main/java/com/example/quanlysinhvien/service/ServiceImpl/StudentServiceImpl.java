package com.example.quanlysinhvien.service.ServiceImpl;

import com.example.quanlysinhvien.dto.StudentDTO;
import com.example.quanlysinhvien.exception.UserNotFoundException;
import com.example.quanlysinhvien.model.Student;
import com.example.quanlysinhvien.repository.StudentRepository;
import com.example.quanlysinhvien.service.StudentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<StudentDTO> listAll() {
        List<Student> students = studentRepository.findAll();
        return students.stream().map((student) -> modelToDto(student)).collect(Collectors.toList());
    }

    @Override
    public Student getStudentById(Long id) throws UserNotFoundException {
        Optional<Student> student = studentRepository.findById(id);
        if (student.isPresent()) {
            return student.get();
        }
        throw new UserNotFoundException("Could not find any student with id" + id);
    }

    @Override
    public Student getStudentBySid(String sid) throws UserNotFoundException {
        Optional<Student> student = studentRepository.findStudentBySid(sid);
        if (student.isPresent()) {
            return student.get();
        }
        throw new UserNotFoundException("Could not find any student with id" + sid);
    }

    @Override
    public Student createStudent(StudentDTO studentDto) {
        Student student = dtoToModel(studentDto);
        return studentRepository.save(student);
    }

    @Override
    public void save(StudentDTO studentDTO) {
        Student student = dtoToModel(studentDTO);
        studentRepository.save(student);
    }

    @Override
    public void softDeleteStudent(String sid) throws UserNotFoundException {
        Optional<Student> optionalStudent = studentRepository.findStudentBySid(sid);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            student.setDeleted(true);
            studentRepository.save(student);
        } else {
            throw new UserNotFoundException("Student not found");
        }
    }

    @Override
    public List<Student> search(String keyword) {
        return studentRepository.search(keyword);
    }

    private Student dtoToModel(StudentDTO studentDto) {
        return modelMapper.map(studentDto, Student.class);
    }

    private StudentDTO modelToDto(Student student) {
        return modelMapper.map(student, StudentDTO.class);
    }
}
