package webprogramming.csc1106.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import webprogramming.csc1106.Entities.*;
import webprogramming.csc1106.Services.UploadCourseService;
import webprogramming.csc1106.Services.UserService;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Controller
public class MarketplaceUploadController {

    private static final Logger logger = Logger.getLogger(MarketplaceUploadController.class.getName());

    @Autowired
    private UploadCourseService courseService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private UploadCourseService uploadCourseService; // Ensure this line is added

    @GetMapping("/coursesupload")
    public String showCoursesPage(@RequestParam(value = "categoryId", required = false) Long categoryId, Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login"; // Redirect to login if user is not logged in
        }

        if (categoryId != null) {
            model.addAttribute("courses", courseService.getApprovedCoursesByCategoryId(categoryId));
        } else {
            model.addAttribute("courses", courseService.getApprovedCoursesByUserId(userId));
        }
        return "Marketplace/coursesupload";
    }

    // Displays the upload course page with a new course object and list of categories
    @GetMapping("/upload")
    public String showUploadPage(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login"; // Redirect to login if user is not logged in
        }
        model.addAttribute("course", new UploadCourse());
        model.addAttribute("categories", courseService.getAllCategories());
        return "Marketplace/upload";
    }

    // Handles the course upload form submission
    @PostMapping("/upload")
    public String uploadCourse(@ModelAttribute UploadCourse course,
                               @RequestParam("coverImage") MultipartFile coverImage,
                               @RequestParam("selectedCategory") Long selectedCategory,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                return "redirect:/login"; // Redirect to login if user is not logged in
            }

            User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            course.setUser(user);
            course.setApproved(false);  // Set course as not approved initially
            courseService.processCourseUpload(course, coverImage, selectedCategory);
            redirectAttributes.addFlashAttribute("successMessage", "Course uploaded successfully.");
        } catch (IOException e) {
            logger.severe("Failed to upload the file: " + e.getMessage());
            model.addAttribute("errorMessage", "Failed to upload the file. Please try again.");
            return "Marketplace/upload";
        } catch (RuntimeException e) {
            logger.severe("Runtime exception: " + e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "Marketplace/upload";
        }
        return "redirect:/coursesupload";
    }

    // Displays the page with the list of pending courses for approval
    @GetMapping("/pending-courses")
    public String showPendingCourses(Model model) {
        List<UploadCourse> pendingCourses = courseService.getPendingCourses();
        model.addAttribute("pendingCourses", pendingCourses);
        return "Admin/pending-courses"; 
    }

    // Approves a specific course by setting its approved status to true
    @PostMapping("/courses/{courseId}/approve")
    public String approveCourse(@PathVariable Long courseId, RedirectAttributes redirectAttributes) {
        UploadCourse course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setApproved(true);
        courseService.updateCourse(course);
        redirectAttributes.addFlashAttribute("successMessage", "Course approved successfully.");
        return "redirect:/pending-courses";
    }
    
    // Rejects a specific course by setting its approved status to false
    @PostMapping("/courses/{courseId}/reject")
    public String rejectCourse(@PathVariable Long courseId, RedirectAttributes redirectAttributes) {
        UploadCourse course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setApproved(false);
        courseService.updateCourse(course);
        redirectAttributes.addFlashAttribute("successMessage", "Course rejected successfully.");
        return "redirect:/pending-courses";
    }
    
    // Serves a file for download or inline display based on the disposition
    @GetMapping("/serveFile")
    public ResponseEntity<InputStreamResource> serveFile(@RequestParam("fileId") Long fileId, @RequestParam("disposition") String disposition) throws IOException {
        FileResource fileResource = courseService.getFileResourceById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        InputStream fileInputStream = courseService.downloadFileWithSas(fileResource.getFileUrl());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, disposition + "; filename=\"" + fileResource.getFileName() + "\"");
        headers.setContentType(getMediaTypeForFileName(fileResource.getFileName()));

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(fileInputStream));
    }

    // Determines the media type for a file based on its name
    private MediaType getMediaTypeForFileName(String fileName) {
        String mimeType = URLConnection.guessContentTypeFromName(fileName);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        return MediaType.parseMediaType(mimeType);
    }

    // Displays the edit course page with the course details and categories
    @GetMapping("/courses/edit")
    public String showEditPage(@RequestParam("courseId") Long courseId, Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login"; // Redirect to login if user is not logged in
        }
        Optional<UploadCourse> courseOptional = courseService.getCourseById(courseId);
        if (courseOptional.isPresent()) {
            model.addAttribute("course", courseOptional.get());
            model.addAttribute("categories", courseService.getAllCategories());
            return "Marketplace/edit";
        } else {
            return "redirect:/coursesupload";
        }
    }

    // Handles the course update form submission
    @PostMapping("/courses/update")
    public String updateCourse(@ModelAttribute UploadCourse course,
                               @RequestParam("coverImage") MultipartFile coverImage,
                               @RequestParam("selectedCategory") Long selectedCategory,
                               RedirectAttributes redirectAttributes) {
        try {
            courseService.processCourseUpdate(course, coverImage, selectedCategory);
            redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully.");
        } catch (IOException e) {
            logger.severe("Failed to update the file: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the file. Please try again.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/courses/edit?courseId=" + course.getId();
    }

    // Deletes a specific course by its ID
    @PostMapping("/courses/delete")
    public String deleteCourse(@RequestParam("courseId") Long courseId, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(courseId);
            redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully.");
        } catch (Exception e) {
            logger.severe("Failed to delete the course: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete the course. Please try again.");
        }
        return "redirect:/coursesupload";
    }

    // Handles the section removal request
    @PostMapping("/courses/removeSection")
    public String removeSection(@RequestParam("sectionId") Long sectionId, @RequestParam("courseId") Long courseId, RedirectAttributes redirectAttributes) {
        try {
            courseService.removeSection(sectionId, courseId);
            redirectAttributes.addFlashAttribute("successMessage", "Section removed successfully.");
        } catch (Exception e) {
            logger.severe("Failed to remove the section: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to remove the section. Please try again.");
        }
        return "redirect:/courses/edit?courseId=" + courseId;
    }


    // Handles the lesson removal request
    @PostMapping("/courses/removeLesson")
    public String removeLesson(@RequestParam("lessonId") Long lessonId, @RequestParam("courseId") Long courseId, RedirectAttributes redirectAttributes) {
        try {
            courseService.removeLesson(lessonId, courseId);
            redirectAttributes.addFlashAttribute("successMessage", "Lesson removed successfully.");
        } catch (Exception e) {
            logger.severe("Failed to remove the lesson: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to remove the lesson. Please try again.");
        }
        return "redirect:/courses/edit?courseId=" + courseId;
    }

    // Displays the category page with the category details and its courses
    @GetMapping("/category/{id}")
    public String getCategoryPage(@PathVariable("id") Long id, Model model) {
        Optional<CategoryGroup> category = courseService.getCategoryById(id);
        if (category.isPresent()) {
            model.addAttribute("category", category.get());
        } else {
            model.addAttribute("category", new CategoryGroup());
        }
        model.addAttribute("categories", courseService.getAllCategories()); // For the filter dropdown
        return "Marketplace/category-page"; // Ensure this matches your Thymeleaf template name
    }
    // New API endpoint to get category data as JSON
    @GetMapping("/api/category/{id}")
    @ResponseBody
    public ResponseEntity<CategoryGroup> getCategoryById(@PathVariable("id") Long id) {
        Optional<CategoryGroup> category = courseService.getCategoryById(id);
        if (category.isPresent()) {
            return ResponseEntity.ok(category.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    // API endpoint to get courses by category ID with optional sorting
    @GetMapping("/category/{id}/courses")
    @ResponseBody
    public ResponseEntity<List<UploadCourse>> getCoursesByCategory(
            @PathVariable("id") Long categoryId,
            @RequestParam(value = "sortBy", required = false) String sortBy) {
        try {
            List<UploadCourse> courses = uploadCourseService.getFilteredAndSortedCourses(categoryId, sortBy)
                    .stream()
                    .filter(UploadCourse::isApproved)
                    .peek(uploadCourseService::calculateRating) // Calculate rating before returning
                    .collect(Collectors.toList());
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}
