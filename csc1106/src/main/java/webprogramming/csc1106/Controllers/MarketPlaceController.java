package webprogramming.csc1106.Controllers;

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

@Controller
@RequestMapping("/market")
public class MarketPlaceController {

    @Autowired
    private UploadCourseService courseService;

    @Autowired
    private CategoryGroupService categoryGroupService;

    private static final Logger logger = Logger.getLogger(MarketPlaceController.class.getName());

    @GetMapping
    public String showMarketPlacePage(Model model) {
        return "Marketplace/market";
    }


    @GetMapping("/categories")
    @ResponseBody
    public ResponseEntity<List<CategoryGroup>> getCategories() {
        try {
            List<CategoryGroup> categories = categoryGroupService.getAllCategoryGroups();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.severe("Error fetching categories: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/totalCourses")
    @ResponseBody
    public ResponseEntity<Long> getTotalCourses() {
        try {
            long totalCourses = courseService.getTotalCourses();
            return ResponseEntity.ok(totalCourses);
        } catch (Exception e) {
            logger.severe("Error    ing total courses: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/totalApprovedCourses")
    @ResponseBody
    public ResponseEntity<Long> getTotalApprovedCourses() {
        try {
            long totalApprovedCourses = courseService.getAllApprovedCourses().size();
            return ResponseEntity.ok(totalApprovedCourses);
        } catch (Exception e) {
            logger.severe("Error fetching total approved courses: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/checkout")
    public String redirectToCheckout() {
        return "Marketplace/checkout";
    }

    
}
