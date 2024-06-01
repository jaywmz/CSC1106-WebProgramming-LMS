package webprogramming.csc1106.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import webprogramming.csc1106.Entities.UploadCourse;
import webprogramming.csc1106.Services.UploadCourseService;

import java.util.List;
import java.util.logging.Logger;

@Controller
public class MarketPlaceController {

    @Autowired
    private UploadCourseService courseService;

    private static final Logger logger = Logger.getLogger(MarketPlaceController.class.getName());

    @GetMapping("/market")
    public String showMarketPlacePage(Model model) {
        long totalCourses = courseService.getTotalCourses();
        List<UploadCourse> courses = courseService.getAllCourses();
        model.addAttribute("totalCourses", totalCourses);
        model.addAttribute("courses", courses);

        // Log the courses
        logger.info("Total courses: " + totalCourses);
        for (UploadCourse course : courses) {
            logger.info("Course: " + course.getTitle());
        }

        return "market";
    }

    @GetMapping("/checkout")
    public String redirectToCheckout() {
        return "checkout";
    }

    @GetMapping("/IT_Software")
    public String redirectToITandSoftware() {
        return "IT_Software";
    }

    @GetMapping("/business")
    public String redirectToBusiness(Model model) {
        long totalCourses = courseService.getTotalCourses();
        model.addAttribute("totalCourses", totalCourses);
        return "business";
    }
}
