package webprogramming.csc1106.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import webprogramming.csc1106.Entities.UploadCourse;
import webprogramming.csc1106.Services.UploadCourseService;

import java.io.IOException;
import java.io.InputStream;

@Controller
public class UploadController {
    @Autowired
    private UploadCourseService courseService;

    @GetMapping("/coursesupload")
    public String showCoursesPage(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "coursesupload";
    }

    @GetMapping("/upload")
    public String showUploadPage(Model model) {
        model.addAttribute("course", new UploadCourse());
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadCourse(@ModelAttribute UploadCourse course, @RequestParam("file") MultipartFile file, Model model) {
        try {
            courseService.addCourse(course, file.getInputStream(), file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Failed to upload the file. Please try again.");
            return "upload";
        }
        model.addAttribute("courses", courseService.getAllCourses());
        return "redirect:/coursesupload";
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam("courseId") Long courseId) throws IOException {
        // Fetch course metadata from MySQL
        UploadCourse course = courseService.getCourseById(courseId);
        // Retrieve the file from Azure Blob Storage using the blob name
        InputStream fileInputStream = courseService.downloadFile(course.getBlobName());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + course.getBlobName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(fileInputStream));
    }

    @GetMapping("/view")
    public ResponseEntity<InputStreamResource> viewFile(@RequestParam("courseId") Long courseId) throws IOException {
        // Fetch course metadata from MySQL
        UploadCourse course = courseService.getCourseById(courseId);
        // Retrieve the file from Azure Blob Storage using the blob name
        InputStream fileInputStream = courseService.downloadFile(course.getBlobName());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + course.getBlobName() + "\"")
                .contentType(MediaType.APPLICATION_PDF) // Set this dynamically if needed
                .body(new InputStreamResource(fileInputStream));
    }
}
