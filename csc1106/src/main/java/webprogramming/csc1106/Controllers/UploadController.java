package webprogramming.csc1106.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import webprogramming.csc1106.Entities.UploadCourse;
import webprogramming.csc1106.Services.UploadCourseService;

@Controller
public class UploadController {
    @Autowired
    private UploadCourseService courseService;

    @GetMapping("/courses")
    public String showCoursesPage(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "courses";
    }

    @GetMapping("/upload")
    public String showUploadPage(Model model) {
        model.addAttribute("course", new UploadCourse());
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadCourse(@ModelAttribute UploadCourse course, Model model) {
        courseService.addCourse(course);
        model.addAttribute("courses", courseService.getAllCourses());
        return "redirect:/courses";
    }
}
