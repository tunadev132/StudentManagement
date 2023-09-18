package com.example.quanlysinhvien.service.ServiceImpl;

import com.example.quanlysinhvien.dto.CourseDTO;
import com.example.quanlysinhvien.model.ClassM;
import com.example.quanlysinhvien.model.Course;
import com.example.quanlysinhvien.model.Student;
import com.example.quanlysinhvien.repository.CourseRepository;
import com.example.quanlysinhvien.service.CourseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<CourseDTO> listAll() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream().map((course) -> modelToDto(course)).collect(Collectors.toList());
    }

    @Override
    public void save(CourseDTO courseDTO) {
        Course course = dtoToModel(courseDTO);
        courseRepository.save(course);
    }

    @Override
    public Course createCourse(CourseDTO courseDTO) {
        Course course = dtoToModel(courseDTO);
        return courseRepository.save(course);
    }

    @Override
    public Course get(Long id) {
        return courseRepository.findById(id).get();
    }

    @Override
    public Course findByCourseName(String name) {
        return courseRepository.findByCourseName(name);
    }

    @Override
    public void delete(Long id) throws ChangeSetPersister.NotFoundException {
        Course course = courseRepository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        if (course.getClasses().isEmpty()) {
            courseRepository.delete(course);
        } else {
            throw new IllegalStateException("Cannot delete course because it has classes.");
        }
    }

    private Course dtoToModel(CourseDTO courseDto) {
        return modelMapper.map(courseDto, Course.class);
    }

    private CourseDTO modelToDto(Course course) {
        return modelMapper.map(course, CourseDTO.class);
    }
}
