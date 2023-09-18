package com.example.quanlysinhvien.service.ServiceImpl;

import com.example.quanlysinhvien.dto.ClassCourseDTO;
import com.example.quanlysinhvien.dto.ClassDTO;
import com.example.quanlysinhvien.dto.StudentClassDTO;
import com.example.quanlysinhvien.model.ClassM;
import com.example.quanlysinhvien.repository.ClassRepository;
import com.example.quanlysinhvien.service.ClassService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Component
public class ClassServiceImpl implements ClassService {
    @Autowired
    private ClassRepository classRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<ClassM> listAll() {
        return classRepository.findAll();
    }

    @Override
    public void save(ClassM classes) {
        classRepository.save(classes);
    }

    @Override
    public ClassM get(Long id) {
        return classRepository.findById(id).get();
    }

    @Override
    public void delete(Long id) throws ChangeSetPersister.NotFoundException {
        ClassM clazz = classRepository.findById(id)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        if (clazz.getStudents().isEmpty()) {
            classRepository.delete(clazz);
        } else {
            throw new IllegalStateException("Cannot delete class with registered students.");
        }
    }

    @Override
    public List<ClassCourseDTO> findClass() {
        return classRepository.findClass();
    }

    @Override
    public List<StudentClassDTO> listClassOfStudent(Long id) {
        List<Object[]> resultList = classRepository.listClassOfStudent(id);

        List<StudentClassDTO> dtoList = new ArrayList<>();
        for (Object[] result : resultList) {
            StudentClassDTO dto = new StudentClassDTO();
            dto.setStudentId(((BigInteger) result[0]).longValue());
            dto.setClassId(((BigInteger) result[1]).longValue());
            dto.setClassName((String) result[2]);
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public List<ClassCourseDTO> listClassNotRegist(Long id) {
        List<Object[]> resultList = classRepository.listClassNotRegist(id);

        List<ClassCourseDTO> dtoList = new ArrayList<>();
        for (Object[] result : resultList) {
            ClassCourseDTO dto = new ClassCourseDTO();
            dto.setId(((BigInteger) result[0]).longValue()); // Ánh xạ trường đầu tiên của Object[] sang trường dtoId trong StudentClassDTO
            dto.setClassName((String) result[1]); // Ánh xạ trường thứ hai của Object[] sang trường dtoName trong StudentClassDTO
            dto.setCourseName((String) result[2]);
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public void deleteClassRegistered(Long studentId, Long classId) {
        classRepository.unregistClass(studentId, classId);
    }
}
