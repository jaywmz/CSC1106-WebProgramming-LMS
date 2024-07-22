package webprogramming.csc1106.Controllers;

// Import necessary packages and classes
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import webprogramming.csc1106.Entities.CategoryGroup;
import webprogramming.csc1106.Services.UploadCourseService;
import webprogramming.csc1106.Services.CategoryGroupService;

import java.util.List;
import java.util.logging.Logger;

@Controller // Indicate that this class is a Spring MVC controller
@RequestMapping("/market") // Map requests to /market to this controller
public class MarketPlaceController {

    @Autowired
    private UploadCourseService courseService; // Inject the UploadCourseService

    @Autowired
    private CategoryGroupService categoryGroupService; // Inject the CategoryGroupService

    private static final Logger logger = Logger.getLogger(MarketPlaceController.class.getName()); // Initialize a logger

    // Handle GET requests to /market URL
    @GetMapping
    public String showMarketPlacePage(Model model) {
        return "Marketplace/market"; // Return the "market" view
    }

    // Handle GET requests to /market/categories URL and return a list of category groups as JSON
    @GetMapping("/categories")
    @ResponseBody
    public ResponseEntity<List<CategoryGroup>> getCategories() {
        try {
            List<CategoryGroup> categories = categoryGroupService.getAllCategoryGroups(); // Get all category groups
            return ResponseEntity.ok(categories); // Return the categories in the response body with 200 OK status
        } catch (Exception e) {
            // Handle errors and log the exception message
            logger.severe("Error fetching categories: " + e.getMessage());
            return ResponseEntity.status(500).build(); // Return 500 Internal Server Error status
        }
    }

    // Handle GET requests to /market/totalCourses URL and return the total number of courses as JSON
    @GetMapping("/totalCourses")
    @ResponseBody
    public ResponseEntity<Long> getTotalCourses() {
        try {
            long totalCourses = courseService.getTotalCourses(); // Get the total number of courses
            return ResponseEntity.ok(totalCourses); // Return the total number of courses in the response body with 200 OK status
        } catch (Exception e) {
            // Handle errors and log the exception message
            logger.severe("Error fetching total courses: " + e.getMessage());
            return ResponseEntity.status(500).build(); // Return 500 Internal Server Error status
        }
    }

    // Handle GET requests to /market/totalApprovedCourses URL and return the total number of approved courses as JSON
    @GetMapping("/totalApprovedCourses")
    @ResponseBody
    public ResponseEntity<Long> getTotalApprovedCourses() {
        try {
            long totalApprovedCourses = courseService.getAllApprovedCourses().size(); // Get the total number of approved courses
            return ResponseEntity.ok(totalApprovedCourses); // Return the total number of approved courses in the response body with 200 OK status
        } catch (Exception e) {
            // Handle errors and log the exception message
            logger.severe("Error fetching total approved courses: " + e.getMessage());
            return ResponseEntity.status(500).build(); // Return 500 Internal Server Error status
        }
    }

}
