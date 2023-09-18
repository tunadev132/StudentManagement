package com.example.quanlysinhvien.repository;

import com.example.quanlysinhvien.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByIsDeletedFalse();

    Optional<Student> findStudentBySid(String sid);

    @Query(value = "SELECT s FROM Student s WHERE s.name LIKE '%' || :keyword || '%'"
            + " OR s.email LIKE '%' || :keyword || '%'")
    public List<Student> search(@Param("keyword") String keyword);
}
