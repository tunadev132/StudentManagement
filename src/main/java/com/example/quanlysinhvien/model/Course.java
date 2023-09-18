package com.example.quanlysinhvien.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String courseName;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date time;
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    @JsonManagedReference
    private List<ClassM> classes;
}