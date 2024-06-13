package webprogramming.csc1106.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import webprogramming.csc1106.Entities.UploadCourse;
import webprogramming.csc1106.Entities.CategoryGroup;
import webprogramming.csc1106.Services.UploadCourseService;
import webprogramming.csc1106.Services.CategoryGroupService;

import java.util.List;
import java.util.logging.Logger;

@Controller
public class MarketPlaceController {

    @Autowired
    private UploadCourseService courseService;

    @Autowired
    private CategoryGroupService categoryGroupService;

    private static final Logger logger = Logger.getLogger(MarketPlaceController.class.getName());

    @GetMapping("/market")
    public String showMarketPlacePage(Model model) {
        long totalCourses = courseService.getTotalCourses();
        List<UploadCourse> courses = courseService.getAllCourses();
        List<CategoryGroup> categories = categoryGroupService.getAllCategoryGroups();

        model.addAttribute("totalCourses", totalCourses);
        model.addAttribute("courses", courses);
        model.addAttribute("categories", categories);

        // Log the courses and categories
        logger.info("Total courses: " + totalCourses);
        for (UploadCourse course : courses) {
            logger.info("Course: " + course.getTitle());
        }
        logger.info("Total categories: " + categories.size());
        for (CategoryGroup category : categories) {
            logger.info("Category: " + category.getName());
        }

        return "Marketplace/market";
    }
    
    @GetMapping("/checkout")
    public String redirectToCheckout() {
        return "Marketplace/checkout";
    }

    
}
