package com.example.quanlysinhvien.controller;

import com.example.quanlysinhvien.dto.ClassCourseDTO;
import com.example.quanlysinhvien.dto.CourseDTO;
import com.example.quanlysinhvien.dto.StudentDTO;
import com.example.quanlysinhvien.exception.UserNotFoundException;
import com.example.quanlysinhvien.model.Course;
import com.example.quanlysinhvien.model.Student;
import com.example.quanlysinhvien.service.CourseService;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private HazelcastInstance hazelcastInstance;

    @GetMapping
    public String coursePage(Model model) {
        IMap<String, List<CourseDTO>> courseMap = hazelcastInstance.getMap("manage-map");
        List<CourseDTO> courseLists;
        if (courseMap.containsKey("courseLists")) {
            courseLists = courseMap.get("courseLists");
        } else {
            courseLists = courseService.listAll();
            courseMap.put("courseLists", courseLists);
        }
        model.addAttribute("courseLists", courseLists);

        return "course";
    }

    @GetMapping("/new")
    public String createCourse(Model model) {
        model.addAttribute("action", "add");
        model.addAttribute("course", new CourseDTO());
        return "course_add";
    }

    @PostMapping("/save")
    public String saveCourse(@RequestParam("action") String action, CourseDTO course, RedirectAttributes ra) {
        courseService.createCourse(course);
        IMap<String, List<CourseDTO>> courseMap = hazelcastInstance.getMap("manage-map");

        List<CourseDTO> courseList;
        if (courseMap.containsKey("courseLists")) {
            courseList = courseMap.get("courseLists");
        } else {
            courseList = courseService.listAll();
        }

        if ("add".equals(action)) {
            boolean containsId = courseList.stream()
                    .anyMatch(s -> s.getCourseName().equals(course.getCourseName()));

            if (!containsId) {
                courseService.save(course);
                courseList.add(course);
            }
        } else if ("edit".equals(action)) {
            for (int i = 0; i < courseList.size(); i++) {
                if (courseList.get(i).getCourseName().equals(course.getCourseName())) {
                    courseService.save(course);
                    courseList.set(i, course);
                    break;
                }
            }
        }
        // Cập nhật Hazelcast Map
        courseMap.put("courseLists", courseList);
        ra.addFlashAttribute("success_message", "The course has been saved successfully.");
        return "redirect:/course";
    }

    @GetMapping("/edit/{id}")
    public String editCourse(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Course course = courseService.get(id);
        model.addAttribute("action", "edit");
        model.addAttribute("course", course);
        return "course_add";
    }

    @RequestMapping("/delete/{id}")
    @CacheEvict(value = "courseLists", key = "#id")
    public String deleteCourse(@PathVariable(name = "id") Long id, RedirectAttributes ra) throws ChangeSetPersister.NotFoundException {
        try {
            courseService.delete(id);
            IMap<String, List<CourseDTO>> courseMap = hazelcastInstance.getMap("manage-map");
            List<CourseDTO> courseList = courseMap.get("courseLists");
            if (courseList != null) {
                courseList.removeIf(course -> course.getId() == id);
                courseMap.put("courseLists", courseList);
            }
            ra.addFlashAttribute("error_message", "The course has been deleted successfully.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/course";
    }
}
