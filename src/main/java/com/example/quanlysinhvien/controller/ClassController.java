package com.example.quanlysinhvien.controller;

import com.example.quanlysinhvien.dto.ClassCourseDTO;
import com.example.quanlysinhvien.dto.CourseDTO;
import com.example.quanlysinhvien.dto.StudentDTO;
import com.example.quanlysinhvien.model.ClassM;
import com.example.quanlysinhvien.model.Course;
import com.example.quanlysinhvien.repository.ClassRepository;
import com.example.quanlysinhvien.service.ClassService;
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

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/class")
public class ClassController {
    @Autowired
    private ClassService classService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private HazelcastInstance hazelcastInstance;

    @GetMapping
    public String classPage(Model model) {
        IMap<String, List<ClassCourseDTO>> classMap = hazelcastInstance.getMap("manage-map");
        List<ClassCourseDTO> classList;
        if (classMap.containsKey("classList")) {
            classList = classMap.get("classList");
        } else {
            classList = classService.findClass();
            classMap.put("classList", classList);
        }
        model.addAttribute("classList", classList);

        return "class";
    }

    @GetMapping("/new")
    public String addCourse(Model model) {
        List<CourseDTO> courseList = courseService.listAll();
        model.addAttribute("courseList", courseList);
        model.addAttribute("class", new ClassM());
        return "class_add";
    }

    @PostMapping(value = "/save")
    public String saveClass(@ModelAttribute("class") ClassM classM, RedirectAttributes ra) {
        classService.save(classM);
        ra.addFlashAttribute("success_message", "The class has been saved successfully.");
        return "redirect:/class";
    }

    @GetMapping("/edit/{id}")
    public String editClass(@PathVariable Long id, Model model, RedirectAttributes ra) {
        ClassM classM = classService.get(id);
        List<CourseDTO> courseList = courseService.listAll();
        model.addAttribute("courseList", courseList);
        model.addAttribute("class", classM);
        return "class_add";
    }

    @RequestMapping("/delete/{id}")
    @CacheEvict(value = "classList", key = "#id")
    public String deleteClassPage(@PathVariable(name = "id") Long id, RedirectAttributes ra) throws ChangeSetPersister.NotFoundException {
        try {
            classService.delete(id);
            IMap<String, List<ClassCourseDTO>> classMap = hazelcastInstance.getMap("manage-map");
            List<ClassCourseDTO> classList = classMap.get("classList");
            if (classList != null) {
                classList.removeIf(class1 -> class1.getId() == id);
                classMap.put("classList", classList);
            }

            ra.addFlashAttribute("error_message", "The class has been deleted successfully.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/class";
    }
}
