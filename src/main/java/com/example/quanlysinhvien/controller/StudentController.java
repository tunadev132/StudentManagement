package com.example.quanlysinhvien.controller;

import com.example.quanlysinhvien.dto.ClassCourseDTO;
import com.example.quanlysinhvien.dto.StudentClassDTO;
import com.example.quanlysinhvien.dto.StudentDTO;
import com.example.quanlysinhvien.exception.UserNotFoundException;
import com.example.quanlysinhvien.model.ClassM;
import com.example.quanlysinhvien.model.Student;
import com.example.quanlysinhvien.repository.ClassRepository;
import com.example.quanlysinhvien.repository.StudentRepository;
import com.example.quanlysinhvien.service.ClassService;
import com.example.quanlysinhvien.service.StudentService;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private ClassService classService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ClassRepository classRepository;
    @Autowired
    private HazelcastInstance hazelcastInstance;

    @GetMapping
    public String studentPage(Model model) {
        IMap<String, List<StudentDTO>> studentMap = hazelcastInstance.getMap("manage-map");
        List<StudentDTO> studentList;
        if (studentMap.containsKey("studentList")) {
            studentList = studentMap.get("studentList");
        } else {
            studentList = studentService.listAll();
            studentMap.put("studentList", studentList);
        }
        model.addAttribute("studentList", studentList);

        return "student";
    }

    @RequestMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        List<Student> result = studentService.search(keyword);
        model.addAttribute("studentList", result);

        return "student";
    }

    @GetMapping("/new")
    public String createStudent(Model model) {
        model.addAttribute("action", "add");
        model.addAttribute("student", new StudentDTO());
        return "student_add";
    }

    @PostMapping("/save")
    public String saveStudent(@RequestParam("action") String action, StudentDTO student, RedirectAttributes ra) {
        IMap<String, List<StudentDTO>> studentMap = hazelcastInstance.getMap("manage-map");

        List<StudentDTO> studentList;
        if (studentMap.containsKey("studentList")) {
            studentList = studentMap.get("studentList");
        } else {
            studentList = studentService.listAll();
        }

        if ("add".equals(action)) {
            // Thêm sinh viên mới vào danh sách nếu chưa có ID trong danh sách
            boolean containsId = studentList.stream()
                    .anyMatch(s -> s.getSid().equals(student.getSid()));

            if (!containsId) {
                // Thực hiện hành động "add"
                studentService.save(student);
                studentList.add(student);
            }
        } else if ("edit".equals(action)) {
            // Sửa đổi thông tin sinh viên nếu có ID trong danh sách
            for (int i = 0; i < studentList.size(); i++) {
                if (studentList.get(i).getSid().equals(student.getSid())) {
                    // Thực hiện hành động "edit"
                    studentService.save(student);
                    studentList.set(i, student);
                    break;
                }
            }
        }
        // Cập nhật Hazelcast Map
        studentMap.put("studentList", studentList);
        ra.addFlashAttribute("success_message", "The student has been saved successfully.");
        return "redirect:/student";
    }

    @GetMapping("/edit/{sid}")
    public String editStudent(@PathVariable String sid, Model model, RedirectAttributes ra) {
        try {
            Student student = studentService.getStudentBySid(sid);
            model.addAttribute("action", "edit");
            model.addAttribute("student", student);
            return "student_add";
        } catch (UserNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/student";
        }
    }

    @GetMapping("/delete/{sid}")
    public String deleteStudent(@PathVariable String sid, RedirectAttributes ra) throws UserNotFoundException {
        IMap<String, List<StudentDTO>> studentMap = hazelcastInstance.getMap("manage-map");
        List<StudentDTO> studentList = studentMap.getOrDefault("studentList", new ArrayList<>());

        // Xác định vị trí của sinh viên trong danh sách
        int index = -1;
        for (int i = 0; i < studentList.size(); i++) {
            if (studentList.get(i).getSid().equals(sid)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            // Xóa sinh viên khỏi danh sách
            studentList.remove(index);
            // Cập nhật Hazelcast Map
            studentMap.put("studentList", studentList);
            // Xóa sinh viên trong backend
            studentService.softDeleteStudent(sid);
            ra.addFlashAttribute("error_message", "The student has been deleted successfully.");
        } else {
            ra.addFlashAttribute("error_message", "The student does not exist.");
        }

        return "redirect:/student";
    }

    @RequestMapping("/class/{id}/view")
    public String showRegistrationClassForm(@PathVariable(name = "id") Long id, Model model) throws UserNotFoundException {
        List<ClassCourseDTO> list = classService.listClassNotRegist(id);
        Student stu = studentService.getStudentById(id);
        model.addAttribute("classList", list);
        model.addAttribute("student", stu);
        return "class_registration";
    }

    @PostMapping("/class/{studentId}/register")
    public String register(@PathVariable @RequestParam("studentId") Long studentId,
                           @RequestParam("classId") Long[] classIds, RedirectAttributes ra) {
        Optional<Student> optionalStudent = studentRepository.findById(studentId);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            List<ClassM> classes = classRepository.findAllById(Arrays.asList(classIds));
            for (ClassM classM : classes) {
                if (!student.getAssignClass().contains(classM)) {
                    student.getAssignClass().add(classM);
                }
            }
            studentRepository.save(student);
        }
        ra.addFlashAttribute("assign_success", "The class has been assigned successfully.");
        return "redirect:/student/class/{studentId}/view";
    }

    @GetMapping("/class/{stuId}/list")
    public String listClassOfStudent(@PathVariable(name = "stuId") Long id, Model model) throws UserNotFoundException {
        List<StudentClassDTO> list = classService.listClassOfStudent(id);
        Student stu = studentService.getStudentById(id);
        model.addAttribute("classList", list);
        model.addAttribute("student", stu);
        return "class_registered";
    }

    @RequestMapping("/class/{studentId}/remove/{classId}")
    public String deleteClassRegistered(@PathVariable(name = "studentId") Long studentId, @PathVariable("classId") Long classId, RedirectAttributes ra) {
        Optional<Student> optionalStudent = studentRepository.findById(studentId);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            ClassM classM = classRepository.findById(classId).orElse(null);

            if (classM != null && student.getAssignClass().contains(classM)) {
                student.getAssignClass().remove(classM);
                studentRepository.save(student);
            }
        }
        ra.addFlashAttribute("assign_remove", "The class has been removed successfully.");
        return "redirect:/student/class/{studentId}/list";
    }
}