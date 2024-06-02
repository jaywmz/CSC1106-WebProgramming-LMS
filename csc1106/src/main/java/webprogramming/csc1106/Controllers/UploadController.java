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
            // Validate file type
            String contentType = coverImage.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                model.addAttribute("errorMessage", "Invalid file type for cover image. Only images are allowed.");
                return "upload";
            }
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

    @GetMapping("/courses/edit")
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
    public String updateCourse(@ModelAttribute UploadCourse course,
                               @RequestParam("coverImage") MultipartFile coverImage,
                               Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<UploadCourse> existingCourseOpt = courseService.getCourseById(course.getId());
            if (!existingCourseOpt.isPresent()) {
                model.addAttribute("errorMessage", "Course not found.");
                return "edit";
            }

            UploadCourse existingCourse = existingCourseOpt.get();

            // Update cover image if a new one is provided
            if (coverImage != null && !coverImage.isEmpty()) {
                // Validate file type
                String contentType = coverImage.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    model.addAttribute("errorMessage", "Invalid file type for cover image. Only images are allowed.");
                    return "edit";
                }
                // Delete the existing cover image if it exists
                if (existingCourse.getCoverImageUrl() != null) {
                    courseService.deleteBlob(existingCourse.getCoverImageUrl());
                }
                // Upload the new cover image
                String coverImageUrl = courseService.uploadToAzureBlob(coverImage.getInputStream(), coverImage.getOriginalFilename());
                coverImageUrl = courseService.generateSasUrl(coverImageUrl);
                existingCourse.setCoverImageUrl(coverImageUrl);
            }

            // Update basic fields
            existingCourse.setTitle(course.getTitle());
            existingCourse.setDescription(course.getDescription());
            existingCourse.setLecturer(course.getLecturer());
            existingCourse.setPrice(course.getPrice());

            // Clear and update sections and lessons
            existingCourse.getSections().clear();
            for (Section section : course.getSections()) {
                section.setCourse(existingCourse);
                Section savedSection = courseService.addSection(section); // Save section first

                for (Lesson lesson : section.getLessons()) {
                    lesson.setSection(savedSection);
                    Lesson savedLesson = courseService.addLesson(lesson); // Save lesson first

                    MultipartFile lessonFile = lesson.getFile();
                    if (lessonFile != null && !lessonFile.isEmpty()) {
                        // Delete existing files associated with the lesson
                        for (FileResource existingFile : lesson.getFiles()) {
                            courseService.deleteBlob(existingFile.getFileUrl());
                        }
                        lesson.getFiles().clear();

                        // Upload the new lesson file
                        String fileUrl = courseService.uploadToAzureBlob(lessonFile.getInputStream(), lessonFile.getOriginalFilename());
                        fileUrl = courseService.generateSasUrl(fileUrl);
                        FileResource fileResource = new FileResource(lessonFile.getOriginalFilename(), fileUrl);
                        fileResource.setLesson(savedLesson); // Associate file with the saved lesson
                        courseService.addFileResource(fileResource);
                        savedLesson.getFiles().add(fileResource); // Add the file to the saved lesson
                    }
                }
                existingCourse.getSections().add(savedSection);
            }

            courseService.updateCourse(existingCourse);
            redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully.");
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Failed to update the file. Please try again.");
            return "edit";
        }
        return "redirect:/coursesupload";
    }

    @PostMapping("/courses/delete")
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
