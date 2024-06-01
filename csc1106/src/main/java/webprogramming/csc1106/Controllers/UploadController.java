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
import webprogramming.csc1106.Entities.FileResource;
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
    public String uploadCourse(@ModelAttribute UploadCourse course, 
                               @RequestParam("coverImage") MultipartFile coverImage, 
                               Model model, 
                               RedirectAttributes redirectAttributes) {
        try {
            if (coverImage != null && !coverImage.isEmpty()) {
                String coverImageUrl = courseService.uploadToAzureBlob(coverImage.getInputStream(), coverImage.getOriginalFilename());
                coverImageUrl = courseService.generateSasUrl(coverImageUrl);
                course.setCoverImageUrl(coverImageUrl);
            }
            courseService.addCourse(course);

            for (Section section : course.getSections()) {
                section.setCourse(course);
                courseService.addSection(section);

                for (Lesson lesson : section.getLessons()) {
                    lesson.setSection(section);
                    courseService.addLesson(lesson);

                    MultipartFile lessonFile = lesson.getFile();
                    if (lessonFile != null && !lessonFile.isEmpty()) {
                        String fileUrl = courseService.uploadToAzureBlob(lessonFile.getInputStream(), lessonFile.getOriginalFilename());
                        fileUrl = courseService.generateSasUrl(fileUrl);
                        FileResource fileResource = new FileResource(lessonFile.getOriginalFilename(), fileUrl);
                        fileResource.setLesson(lesson);
                        courseService.addFileResource(fileResource);
                        lesson.getFiles().add(fileResource);
                    }
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
    public ResponseEntity<InputStreamResource> serveFile(@RequestParam("fileId") Long fileId, @RequestParam("disposition") String disposition) throws IOException {
        FileResource fileResource = courseService.getFileResourceById(fileId).orElseThrow(() -> new RuntimeException("File not found"));
        InputStream fileInputStream = courseService.downloadFileWithSas(fileResource.getFileUrl());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, disposition + "; filename=\"" + fileResource.getFileName() + "\"");
        headers.setContentType(getMediaTypeForFileName(fileResource.getFileName()));

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
    public String viewFile(@RequestParam("fileId") Long fileId) {
        return "redirect:/serveFile?fileId=" + fileId + "&disposition=inline";
    }

    @GetMapping("/download")
    public String downloadFile(@RequestParam("fileId") Long fileId) {
        return "redirect:/serveFile?fileId=" + fileId + "&disposition=attachment";
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

    @PostMapping("/delete")
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
