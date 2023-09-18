package com.example.quanlysinhvien.repository;

import com.example.quanlysinhvien.dto.ClassCourseDTO;
import com.example.quanlysinhvien.dto.ClassDTO;
import com.example.quanlysinhvien.dto.StudentClassDTO;
import com.example.quanlysinhvien.model.ClassM;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<ClassM, Long> {
    @Query("SELECT new com.example.quanlysinhvien.dto.ClassCourseDTO(c.id, c.name, co.courseName) FROM ClassM c JOIN c.course co")
    List<ClassCourseDTO> findClass();

    @Query(value = "select sc.student_id, sc.class_id, c.name" + " from student_class sc join classes c on sc.class_id = c.id "+"where student_id = :studentId", nativeQuery = true)
    List<Object[]> listClassOfStudent(@Param("studentId") Long id);

    @Query(value = "SELECT c.id, c.name, co.course_name " +
            "FROM classes c JOIN courses co ON c.course_id = co.id " +
            "WHERE c.id NOT IN ( " +
            "    SELECT class_id " +
            "    FROM student_class " +
            "    WHERE student_id = :param) ", nativeQuery = true)
    List<Object[]> listClassNotRegist(@Param("param") Long id);

    @Query(value = "DELETE FROM student_class " + "WHERE student_id = :studentId and class_id = :classId", nativeQuery = true)
    void unregistClass(@Param("studentId") Long studentId, @Param("classId") Long classId);
}
