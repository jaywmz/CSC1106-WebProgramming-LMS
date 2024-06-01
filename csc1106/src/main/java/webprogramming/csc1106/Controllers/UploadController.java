package webprogramming.csc1106.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import webprogramming.csc1106.Entities.Lesson;
import webprogramming.csc1106.Entities.Section;
import webprogramming.csc1106.Entities.UploadCourse;
import webprogramming.csc1106.Services.UploadCourseService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Optional;

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
    public String uploadCourse(@ModelAttribute UploadCourse course, @RequestParam("file") MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty()) {
                courseService.addCourse(course);
            } else {
                courseService.addCourseWithFile(course, file.getInputStream(), file.getOriginalFilename());
            }

            for (Section section : course.getSections()) {
                section.setCourse(course);
                courseService.addSection(section);

                for (Lesson lesson : section.getLessons()) {
                    MultipartFile lessonFile = lesson.getFile();
                    courseService.addLesson(lesson, lessonFile.getInputStream(), lessonFile.getOriginalFilename());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Failed to upload the file. Please try again.");
            return "upload";
        }
        return "redirect:/coursesupload";
    }

    @GetMapping("/serveFile")
    public ResponseEntity<InputStreamResource> serveFile(@RequestParam("courseId") Long courseId, @RequestParam("disposition") String disposition) throws IOException {
        UploadCourse course = courseService.getCourseById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        InputStream fileInputStream = courseService.downloadFileWithSas(course.getBlobName());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, disposition + "; filename=\"" + course.getBlobName() + "\"");
        headers.setContentType(getMediaTypeForFileName(course.getBlobName()));

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(fileInputStream));
    }

    private MediaType getMediaTypeForFileName(String fileName) {
        String mimeType = URLConnection.guessContentTypeFromName(fileName);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        return MediaType.parseMediaType(mimeType);
    }

    @GetMapping("/view")
    public String viewFile(@RequestParam("courseId") Long courseId) {
        return "redirect:/serveFile?courseId=" + courseId + "&disposition=inline";
    }

    @GetMapping("/download")
    public String downloadFile(@RequestParam("courseId") Long courseId) {
        return "redirect:/serveFile?courseId=" + courseId + "&disposition=attachment";
    }

    @GetMapping("/edit")
    public String showEditPage(@RequestParam("courseId") Long courseId, Model model) {
        Optional<UploadCourse> courseOptional = courseService.getCourseById(courseId);
        if (courseOptional.isPresent()) {
            model.addAttribute("course", courseOptional.get());
            return "edit";
        } else {
            return "redirect:/coursesupload";
        }
    }

    @PostMapping("/update")
    public String updateCourse(@ModelAttribute UploadCourse course, @RequestParam("file") MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (!file.isEmpty()) {
                courseService.addCourseWithFile(course, file.getInputStream(), file.getOriginalFilename());
            } else {
                courseService.updateCourse(course);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully.");
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Failed to update the file. Please try again.");
            return "edit";
        }
        return "redirect:/coursesupload";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteCourse(@RequestParam("courseId") Long courseId, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(courseId);
            redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete the course. Please try again.");
        }
        return "redirect:/coursesupload";
    }
}
