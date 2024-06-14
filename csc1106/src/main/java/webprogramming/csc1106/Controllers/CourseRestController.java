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

// import webprogramming.csc1106.Services.CourseService;
// import webprogramming.csc1106.Models.Course;

import webprogramming.csc1106.Entities.CourseEntity;
import webprogramming.csc1106.Entities.CourseCategoriesEntity;
import webprogramming.csc1106.Entities.CourseSubscriptionEntity;

import webprogramming.csc1106.Repositories.CoursesRepo;
import webprogramming.csc1106.Repositories.CourseCategoriesRepo;
import webprogramming.csc1106.Repositories.CourseSubscriptionRepo;

@RestController
public class CourseRestController {
    private final CoursesRepo coursesRepo;
    private final CourseCategoriesRepo courseCategoriesRepo;
    private final CourseSubscriptionRepo courseSubscriptionRepo;

    private static final Logger logger = LoggerFactory.getLogger(CourseRestController.class);
    @Autowired
    public CourseRestController(CoursesRepo coursesRepo, CourseCategoriesRepo courseCategoriesRepo, CourseSubscriptionRepo courseSubscriptionRepo) {
        this.coursesRepo = coursesRepo;
        this.courseCategoriesRepo = courseCategoriesRepo;
        this.courseSubscriptionRepo = courseSubscriptionRepo;
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseEntity>> getAllCourses() {
        List<CourseEntity> courses = coursesRepo.getAllCourses();
        if (courses.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @GetMapping("/course/subscription/user/{id}")
    public ResponseEntity<List<CourseSubscriptionEntity>> getCoursesByUserId(@PathVariable("id") int id) {
        List<CourseSubscriptionEntity> courses = courseSubscriptionRepo.findByUserId(id);
        if (courses.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }
    
    @GetMapping("course/subscription/all")
    public ResponseEntity<List<CourseSubscriptionEntity>> getAllCourseSubscriptions() {
        List<CourseSubscriptionEntity> courses = courseSubscriptionRepo.getAllCourseSubscriptions();
        if (courses.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }
}
