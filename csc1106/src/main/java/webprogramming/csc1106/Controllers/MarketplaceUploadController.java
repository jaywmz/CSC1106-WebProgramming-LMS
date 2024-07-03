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
import webprogramming.csc1106.Services.CartService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Controller
public class MarketplaceUploadController {

    private static final Logger logger = Logger.getLogger(MarketplaceUploadController.class.getName());

    @Autowired
    private UploadCourseService courseService;

    @Autowired
    private CartService cartService;

    @GetMapping("/coursesupload")
    public String showCoursesPage(@RequestParam(value = "categoryId", required = false) Long categoryId, Model model) {
        if (categoryId != null) {
            model.addAttribute("courses", courseService.getApprovedCoursesByCategoryId(categoryId));
        } else {
            model.addAttribute("courses", courseService.getApprovedCourses());
        }
        return "Marketplace/coursesupload";
    }

    @GetMapping("/upload")
    public String showUploadPage(Model model) {
        model.addAttribute("course", new UploadCourse());
        model.addAttribute("categories", courseService.getAllCategories());
        return "Marketplace/upload";
    }

    @PostMapping("/upload")
    public String uploadCourse(@ModelAttribute UploadCourse course,
                               @RequestParam("coverImage") MultipartFile coverImage,
                               @RequestParam("selectedCategory") Long selectedCategory,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            course.setApproved(false);
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

    @GetMapping("/pending-courses")
    public String showPendingCourses(Model model) {
        List<UploadCourse> pendingCourses = courseService.getPendingCourses();
        model.addAttribute("pendingCourses", pendingCourses);
        return "/Admin/pending-courses"; 
    }

    @PostMapping("/courses/{courseId}/approve")
    public String approveCourse(@PathVariable Long courseId, RedirectAttributes redirectAttributes) {
        UploadCourse course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setApproved(true);
        courseService.updateCourse(course);
        redirectAttributes.addFlashAttribute("successMessage", "Course approved successfully.");
        return "redirect:/pending-courses";
    }
    
    @PostMapping("/courses/{courseId}/reject")
    public String rejectCourse(@PathVariable Long courseId, RedirectAttributes redirectAttributes) {
        UploadCourse course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setApproved(false);
        courseService.updateCourse(course);
        redirectAttributes.addFlashAttribute("successMessage", "Course rejected successfully.");
        return "redirect:/pending-courses";
    }

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
            return "Marketplace/edit";
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
            courseService.processCourseUpdate(course, coverImage, selectedCategory);
            redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully.");
        } catch (IOException e) {
            logger.severe("Failed to update the file: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the file. Please try again.");
        } catch (RuntimeException e) {
            logger.severe("Runtime exception: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/courses/edit?courseId=" + course.getId();
    }

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

    @GetMapping("/category/{id}")
    public String getCategoryPage(@PathVariable("id") Long id, Model model) {
        Optional<CategoryGroup> category = courseService.getCategoryById(id);
        if (category.isPresent()) {
            model.addAttribute("category", category.get());
            List<UploadCourse> courses = courseService.getCoursesByCategoryId(id);
            model.addAttribute("courses", courses);
        } else {
            model.addAttribute("category", new CategoryGroup());
            model.addAttribute("courses", List.of());
        }
        return "Marketplace/category-page"; // Ensure this matches your Thymeleaf template name
    }

    @GetMapping("/category/{id}/courses")
    @ResponseBody
    public ResponseEntity<List<UploadCourse>> getCoursesByCategory(
            @PathVariable("id") Long categoryId,
            @RequestParam(value = "sortBy", required = false) String sortBy) {
        logger.info("Fetching courses with categoryId: " + categoryId + ", sortBy: " + sortBy);
        try {
            List<UploadCourse> courses = courseService.getFilteredAndSortedCourses(categoryId, sortBy);
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            logger.severe("Error fetching courses: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/cart/add")
    @ResponseBody
    public ResponseEntity<String> addToCart(@RequestParam Long courseId) {
        try {
            Optional<UploadCourse> courseOptional = courseService.getCourseById(courseId);
            if (courseOptional.isPresent()) {
                cartService.addCourseToCart(courseId); // Use the updated method in CartService
                return ResponseEntity.ok("Course added to cart successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
            }
        } catch (Exception e) {
            logger.severe("Failed to add course to cart: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add course to cart. Please try again.");
        }
    }

    @GetMapping("/cart")
public String viewCart(Model model) {
    Cart cart = cartService.getCart();
    logger.info("Cart items: " + cart.getItems());
    for (CartItem item : cart.getItems()) {
        logger.info("Item: " + item.toString());
    }
    model.addAttribute("cart", cart);
    return "Marketplace/cart";
}

    

    @PostMapping("/cart/remove")
    public ResponseEntity<String> removeFromCart(@RequestParam Long courseId) {
        logger.info("Remove from cart called with courseId: " + courseId);
        try {
            cartService.removeCourseFromCart(courseId);
            logger.info("Course removed from cart successfully: " + courseId);
            return ResponseEntity.ok("Course removed from cart successfully.");
        } catch (Exception e) {
            logger.severe("Failed to remove course from cart: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove course from cart. Please try again.");
        }
    }
}
