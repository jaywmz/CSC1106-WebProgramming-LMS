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
import webprogramming.csc1106.Entities.*;
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
        model.addAttribute("categories", courseService.getAllCategories());
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadCourse(@ModelAttribute UploadCourse course,
                               @RequestParam("coverImage") MultipartFile coverImage,
                               @RequestParam("selectedCategory") Long selectedCategory,
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

            // Handle the category
            CategoryGroup categoryGroup = courseService.getCategoryById(selectedCategory)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            CourseCategory courseCategory = new CourseCategory(course, categoryGroup);
            courseService.addCourseCategory(courseCategory);
            course.getCourseCategories().add(courseCategory);

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
            model.addAttribute("categories", courseService.getAllCategories());
            return "edit";
        } else {
            return "redirect:/coursesupload";
        }
    }

    @PostMapping("/courses/update")
    public String updateCourse(@ModelAttribute UploadCourse course,
                               @RequestParam("coverImage") MultipartFile coverImage,
                               @RequestParam("selectedCategory") Long selectedCategory,
                               RedirectAttributes redirectAttributes) {
        try {
            Optional<UploadCourse> existingCourseOpt = courseService.getCourseById(course.getId());
            if (!existingCourseOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Course not found.");
                return "redirect:/courses/edit?courseId=" + course.getId();
            }
    
            UploadCourse existingCourse = existingCourseOpt.get();
    
            // Update cover image if a new one is provided
            if (coverImage != null && !coverImage.isEmpty()) {
                String contentType = coverImage.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Invalid file type for cover image. Only images are allowed.");
                    return "redirect:/courses/edit?courseId=" + course.getId();
                }
                if (existingCourse.getCoverImageUrl() != null) {
                    courseService.deleteBlob(existingCourse.getCoverImageUrl());
                }
                String coverImageUrl = courseService.uploadToAzureBlob(coverImage.getInputStream(), coverImage.getOriginalFilename());
                coverImageUrl = courseService.generateSasUrl(coverImageUrl);
                existingCourse.setCoverImageUrl(coverImageUrl);
            }
    
            // Update basic fields
            existingCourse.setTitle(course.getTitle());
            existingCourse.setDescription(course.getDescription());
            existingCourse.setLecturer(course.getLecturer());
            existingCourse.setPrice(course.getPrice());
    
            // Update the category
            courseService.clearCourseCategories(existingCourse);
            CategoryGroup categoryGroup = courseService.getCategoryById(selectedCategory)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            CourseCategory courseCategory = new CourseCategory(existingCourse, categoryGroup);
            courseService.addCourseCategory(courseCategory);
            existingCourse.getCourseCategories().add(courseCategory);
    
            // Clear and update sections and lessons
            existingCourse.getSections().clear();
            for (Section section : course.getSections()) {
                section.setCourse(existingCourse);
                Section savedSection = courseService.addSection(section);
    
                for (Lesson lesson : section.getLessons()) {
                    lesson.setSection(savedSection);
                    Lesson savedLesson = courseService.addLesson(lesson);
    
                    MultipartFile lessonFile = lesson.getFile();
                    if (lessonFile != null && !lessonFile.isEmpty()) {
                        for (FileResource existingFile : lesson.getFiles()) {
                            courseService.deleteBlob(existingFile.getFileUrl());
                        }
                        lesson.getFiles().clear();
                        String fileUrl = courseService.uploadToAzureBlob(lessonFile.getInputStream(), lessonFile.getOriginalFilename());
                        fileUrl = courseService.generateSasUrl(fileUrl);
                        FileResource fileResource = new FileResource(lessonFile.getOriginalFilename(), fileUrl);
                        fileResource.setLesson(savedLesson);
                        courseService.addFileResource(fileResource);
                        savedLesson.getFiles().add(fileResource);
                    }
                }
                existingCourse.getSections().add(savedSection);
            }
    
            courseService.updateCourse(existingCourse);
            redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the file. Please try again.");
            return "redirect:/courses/edit?courseId=" + course.getId();
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
