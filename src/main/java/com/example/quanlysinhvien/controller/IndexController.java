package com.example.quanlysinhvien.controller;

import com.example.quanlysinhvien.dto.StudentDTO;
import com.example.quanlysinhvien.model.Student;
import com.example.quanlysinhvien.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/")
public class IndexController {
    @GetMapping
    public String homepage() {
        return "index";
    }
}
