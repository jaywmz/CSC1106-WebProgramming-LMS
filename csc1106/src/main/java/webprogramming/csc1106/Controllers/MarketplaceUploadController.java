package webprogramming.csc1106.Controllers;

// Import necessary packages and classes
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

@Controller // Indicate that this class is a Spring MVC controller
public class MarketplaceUploadController {

    private static final Logger logger = Logger.getLogger(MarketplaceUploadController.class.getName()); // Initialize a logger

    @Autowired
    private UploadCourseService courseService; // Inject the UploadCourseService

    @Autowired
    private UserService userService; // Inject the UserService

    @Autowired
    private UploadCourseService uploadCourseService; // Inject the UploadCourseService

    // Handle GET requests to /coursesupload URL
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
        return "Marketplace/coursesupload"; // Return the "coursesupload" view
    }

    // Handle GET requests to /upload URL to display the upload course page
    @GetMapping("/upload")
    public String showUploadPage(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login"; // Redirect to login if user is not logged in
        }
        model.addAttribute("course", new UploadCourse()); // Add a new UploadCourse object to the model
        model.addAttribute("categories", courseService.getAllCategories()); // Add all categories to the model
        return "Marketplace/upload"; // Return the "upload" view
    }

    // Handle POST requests to /upload URL for uploading a course
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
            course.setApproved(false); // Set course as not approved initially
            courseService.processCourseUpload(course, coverImage, selectedCategory);
            redirectAttributes.addFlashAttribute("successMessage", "Course uploaded successfully.");
        } catch (IOException e) {
            logger.severe("Failed to upload the file: " + e.getMessage());
            model.addAttribute("errorMessage", "Failed to upload the file. Please try again.");
            return "Marketplace/upload"; // Return the "upload" view on error
        } catch (RuntimeException e) {
            logger.severe("Runtime exception: " + e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "Marketplace/upload"; // Return the "upload" view on error
        }
        return "redirect:/coursesupload"; // Redirect to the courses upload page on success
    }

    // Handle GET requests to /pending-courses URL to display the list of pending courses for approval
    @GetMapping("/pending-courses")
    public String showPendingCourses(Model model) {
        List<UploadCourse> pendingCourses = courseService.getPendingCourses(); // Get all pending courses
        model.addAttribute("pendingCourses", pendingCourses); // Add pending courses to the model
        return "Admin/pending-courses"; // Return the "pending-courses" view
    }

    // Handle POST requests to /courses/{courseId}/approve URL to approve a specific course
    @PostMapping("/courses/{courseId}/approve")
    public String approveCourse(@PathVariable Long courseId, RedirectAttributes redirectAttributes) {
        UploadCourse course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setApproved(true); // Set course as approved
        courseService.updateCourse(course); // Update the course
        redirectAttributes.addFlashAttribute("successMessage", "Course approved successfully.");
        return "redirect:/pending-courses"; // Redirect to the pending courses page
    }

    // Handle POST requests to /courses/{courseId}/reject URL to reject a specific course
    @PostMapping("/courses/{courseId}/reject")
    public String rejectCourse(@PathVariable Long courseId, RedirectAttributes redirectAttributes) {
        UploadCourse course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setApproved(false); // Set course as not approved
        courseService.updateCourse(course); // Update the course
        redirectAttributes.addFlashAttribute("successMessage", "Course rejected successfully.");
        return "redirect:/pending-courses"; // Redirect to the pending courses page
    }

    // Handle GET requests to /serveFile URL to serve a file for download or inline display
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
                .body(new InputStreamResource(fileInputStream)); // Return the file as a response entity
    }

    // Determines the media type for a file based on its name
    private MediaType getMediaTypeForFileName(String fileName) {
        String mimeType = URLConnection.guessContentTypeFromName(fileName);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        return MediaType.parseMediaType(mimeType);
    }

    // Handle GET requests to /courses/edit URL to display the edit course page
    @GetMapping("/courses/edit")
    public String showEditPage(@RequestParam("courseId") Long courseId, Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login"; // Redirect to login if user is not logged in
        }
        Optional<UploadCourse> courseOptional = courseService.getCourseById(courseId);
        if (courseOptional.isPresent()) {
            model.addAttribute("course", courseOptional.get());
            model.addAttribute("categories", courseService.getAllCategories()); // Add all categories to the model
            return "Marketplace/edit"; // Return the "edit" view
        } else {
            return "redirect:/coursesupload"; // Redirect to the courses upload page if course not found
        }
    }

    // Handle POST requests to /courses/update URL for updating a course
    @PostMapping("/courses/update")
    public String updateCourse(@ModelAttribute UploadCourse course,
                               @RequestParam("coverImage") MultipartFile coverImage,
                               @RequestParam("selectedCategory") Long selectedCategory,
                               RedirectAttributes redirectAttributes) {
        try {
            courseService.processCourseUpdate(course, coverImage, selectedCategory); // Process the course update
            redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully.");
        } catch (IOException e) {
            logger.severe("Failed to update the file: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the file. Please try again.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/courses/edit?courseId=" + course.getId(); // Redirect to the edit page with the course ID
    }

    // Handle POST requests to /courses/delete URL to delete a specific course
    @PostMapping("/courses/delete")
    public String deleteCourse(@RequestParam("courseId") Long courseId, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(courseId); // Delete the course
            redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully.");
        } catch (Exception e) {
            logger.severe("Failed to delete the course: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete the course. Please try again.");
        }
        return "redirect:/coursesupload"; // Redirect to the courses upload page
    }

    // Handle POST requests to /courses/removeSection URL to remove a section from a course
    @PostMapping("/courses/removeSection")
    public String removeSection(@RequestParam("sectionId") Long sectionId, @RequestParam("courseId") Long courseId, RedirectAttributes redirectAttributes) {
        try {
            courseService.removeSection(sectionId, courseId); // Remove the section
            redirectAttributes.addFlashAttribute("successMessage", "Section removed successfully.");
        } catch (Exception e) {
            logger.severe("Failed to remove the section: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to remove the section. Please try again.");
        }
        return "redirect:/courses/edit?courseId=" + courseId; // Redirect to the edit page with the course ID
    }

    // Handle POST requests to /courses/removeLesson URL to remove a lesson from a course
    @PostMapping("/courses/removeLesson")
    public String removeLesson(@RequestParam("lessonId") Long lessonId, @RequestParam("courseId") Long courseId, RedirectAttributes redirectAttributes) {
        try {
            courseService.removeLesson(lessonId, courseId); // Remove the lesson
            redirectAttributes.addFlashAttribute("successMessage", "Lesson removed successfully.");
        } catch (Exception e) {
            logger.severe("Failed to remove the lesson: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to remove the lesson. Please try again.");
        }
        return "redirect:/courses/edit?courseId=" + courseId; // Redirect to the edit page with the course ID
    }

    // Handle GET requests to /category/{id} URL to display the category page
    @GetMapping("/category/{id}")
    public String getCategoryPage(@PathVariable("id") Long id, Model model) {
        Optional<CategoryGroup> category = courseService.getCategoryById(id);
        if (category.isPresent()) {
            model.addAttribute("category", category.get());
        } else {
            model.addAttribute("category", new CategoryGroup());
        }
        model.addAttribute("categories", courseService.getAllCategories()); // Add all categories to the model for filter dropdown
        return "Marketplace/category-page"; // Ensure this matches your Thymeleaf template name
    }

    // Handle GET requests to /api/category/{id} URL to get category data as JSON
    @GetMapping("/api/category/{id}")
    @ResponseBody
    public ResponseEntity<CategoryGroup> getCategoryById(@PathVariable("id") Long id) {
        Optional<CategoryGroup> category = courseService.getCategoryById(id);
        if (category.isPresent()) {
            return ResponseEntity.ok(category.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Return 404 Not Found status if category not found
        }
    }

    // Handle GET requests to /category/{id}/courses URL to get courses by category ID with optional sorting
    @GetMapping("/category/{id}/courses")
    @ResponseBody
    public ResponseEntity<List<UploadCourse>> getCoursesByCategory(
            @PathVariable("id") Long categoryId,
            @RequestParam(value = "sortBy", required = false) String sortBy) {
        try {
            List<UploadCourse> courses = uploadCourseService.getFilteredAndSortedCourses(categoryId, sortBy)
                    .stream()
                    .filter(UploadCourse::isApproved) // Filter only approved courses
                    .peek(uploadCourseService::calculateRating) // Calculate rating before returning
                    .collect(Collectors.toList());
            return ResponseEntity.ok(courses); // Return the courses as a response entity
        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList()); // Return 500 Internal Server Error status
        }
    }
}
