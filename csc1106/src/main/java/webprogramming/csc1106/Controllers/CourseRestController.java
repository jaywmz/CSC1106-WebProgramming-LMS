package webprogramming.csc1106.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List; // Import the List class
import java.util.ArrayList;

import webprogramming.csc1106.Entities.CourseSubscriptionEntity;
import webprogramming.csc1106.Entities.CourseCategory;
import webprogramming.csc1106.Entities.CategoryGroup;
import webprogramming.csc1106.Entities.CartItemEntity;
import webprogramming.csc1106.Entities.UploadCourse;
import webprogramming.csc1106.Entities.User;

import webprogramming.csc1106.Repositories.CourseSubscriptionRepo;
import webprogramming.csc1106.Repositories.CourseCategoryRepository;
import webprogramming.csc1106.Repositories.CartItemsRepo;
import webprogramming.csc1106.Repositories.UploadCourseRepository;
import webprogramming.csc1106.Repositories.CategoryGroupRepository;
import webprogramming.csc1106.Repositories.UserRepository;

@RestController
public class CourseRestController {
    private final UserRepository userRepository;
    private final CourseSubscriptionRepo courseSubscriptionRepo;
    private final CourseCategoryRepository courseCategoryRepository;
    private final CategoryGroupRepository categoryGroupRepository;
    private final CartItemsRepo cartItemsRepo;
    private final UploadCourseRepository uploadCourseRepository;

    private static final Logger logger = LoggerFactory.getLogger(CourseRestController.class);
    @Autowired
    public CourseRestController(UserRepository userRepository, CourseSubscriptionRepo courseSubscriptionRepo, CourseCategoryRepository courseCategoryRepository, CartItemsRepo cartItemsRepo, UploadCourseRepository uploadCourseRepository, CategoryGroupRepository categoryGroupRepository) {
        this.courseSubscriptionRepo = courseSubscriptionRepo;
        this.courseCategoryRepository = courseCategoryRepository;
        this.cartItemsRepo = cartItemsRepo;
        this.uploadCourseRepository = uploadCourseRepository;
        this.categoryGroupRepository = categoryGroupRepository;
        this.userRepository = userRepository;
    }

    // This section will handle the course category
    // Get course category by course id
    @GetMapping("/coursecategory/course/{id}")
    public ResponseEntity<CategoryGroup> getCourseCategoryByCourseId(@PathVariable("id") int id) {
        // Get the category with course id
        CourseCategory category = courseCategoryRepository.findByCourseId((long) id);

        if (category == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(category.getCategoryGroup(), HttpStatus.OK);

    }


    @GetMapping("/course/details/{id}")
    public ResponseEntity<UploadCourse> getCourseDetails(@PathVariable("id") int id) {
        // Get the course details by course id
        UploadCourse course = uploadCourseRepository.findById((long) id).orElse(null);

        if (course == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(course, HttpStatus.OK);
    }

    //This Section will handle the course subscription by user
    // Get all course subscription
    @GetMapping("/course/subscription/all")
    public ResponseEntity<List<CourseSubscriptionEntity>> getAllCourseSubscription() {
        List<CourseSubscriptionEntity> courseSubscription = courseSubscriptionRepo.findAll();
        if (courseSubscription.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(courseSubscription, HttpStatus.OK);
    }

    // Get course subscription by user id
    @GetMapping("/course/subscription/user/{id}")
    public ResponseEntity<List<CourseSubscriptionEntity>> getCourseSubscriptionByUserId(@PathVariable("id") int id) {
        List<CourseSubscriptionEntity> courseSubscription = courseSubscriptionRepo.findByUserId(id);
        if (courseSubscription.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(courseSubscription, HttpStatus.OK);
    }
}
