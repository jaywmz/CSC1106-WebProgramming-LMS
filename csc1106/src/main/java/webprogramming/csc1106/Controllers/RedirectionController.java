package webprogramming.csc1106.Controllers;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import webprogramming.csc1106.Entities.Lesson;
import webprogramming.csc1106.Entities.UploadCourse;
import webprogramming.csc1106.Services.UploadCourseService;
import webprogramming.csc1106.Entities.Section;
import webprogramming.csc1106.Entities.Lesson;

import java.io.IOException;
import java.util.List;



@Controller
public class RedirectionController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/index.html")
    public String indexPage() {
        return "index";
    }
    
    @GetMapping("/blank")
    public String blank() {
        return "Extra/blank";
    }

    @GetMapping("/contact")
    public String contact() {
        return "User/contact";
    }

    @GetMapping("userprofile")
    public String userProfile() {
        return "User/userprofile";
    }
    
    @GetMapping("/refreshsidebar")
    public String refreshSidebar() {
        return "Extra/blank2";
    }

    @GetMapping("/logout")
    public String logout() {
        return "User/logout";
    }

    @GetMapping("/mylearning")
    public String myLearning() {
        return "Course/mylearning";
    }

    @GetMapping("/loading")
    public String loading() {
        return "loading";
    }
    
    @GetMapping("/admin")
    public String adminDashboard() {
        return "Admin/adminDashboard";
    }

    @GetMapping("/partner")
    public String partnerDashboard() {
        return "Partnership/partnerDashboard";
    }

    @GetMapping("/cart")
    public String cart() {
        return "Course/cart";
    }

    @Autowired
    private UploadCourseService courseService;

    @GetMapping("/coursepage")
    public String getCoursePage(@RequestParam("id") Long courseId, Model model) {
        UploadCourse course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        model.addAttribute("course", course);
        return "Course/coursepage";
    }

    @GetMapping("/sectionpage")
    public String getSectionPage(@RequestParam("id") Long sectionId, Model model) {
        Section section = courseService.getSectionById(sectionId);
        if (section != null) {
            System.out.println("Section found: " + section.getTitle());
            List<Lesson> lessons = courseService.getLessonsBySectionId(sectionId);
            System.out.println("Lessons found: " + lessons.size());
            UploadCourse course = courseService.getCourseById(section.getCourse().getId()).orElse(null);
            if (course != null) {
                System.out.println("Course found: " + course.getTitle());
            } else {
                System.out.println("Course not found");
            }

            // Process file resources
            for (Lesson lesson : lessons) {
                for (FileResource file : lesson.getFiles()) {
                    String fileName = file.getFileName().toLowerCase();
                    if (fileName.endsWith(".pdf")) {
                        file.setFileType("pdf");
                    } else if (fileName.endsWith(".txt")) {
                        file.setFileType("text");
                        try {
                            String content = courseService.getFileContent(file.getId());
                            file.setContent(content);
                        } catch (IOException e) {
                            System.err.println("Error reading text file content: " + e.getMessage());
                            // Set a default message or leave content null
                            file.setContent("Error loading file content.");
                        }
                    } else {
                        file.setFileType("other");
                    }
                }
            }

            model.addAttribute("section", section);
            model.addAttribute("lessons", lessons);
            model.addAttribute("course", course);
            return "Course/sectionpage";
        } else {
            System.out.println("Section not found");
            return "redirect:/error";
        }
    }

}