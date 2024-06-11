package webprogramming.csc1106.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List; // Import the List class
import java.util.ArrayList;

// import webprogramming.csc1106.Services.CourseService;
// import webprogramming.csc1106.Models.Course; 
import webprogramming.csc1106.Entities.CourseEntity;
import webprogramming.csc1106.Entities.CourseCategoriesEntity;
import webprogramming.csc1106.Repositories.CoursesRepo;
import webprogramming.csc1106.Repositories.CourseCategoriesRepo;

@RestController
public class CourseRestController {
    private final CoursesRepo coursesRepo;
    private final CourseCategoriesRepo courseCategoriesRepo;
    private static final Logger logger = LoggerFactory.getLogger(CourseRestController.class);
    @Autowired
    public CourseRestController(CoursesRepo coursesRepo, CourseCategoriesRepo courseCategoriesRepo) {
        this.coursesRepo = coursesRepo;
        this.courseCategoriesRepo = courseCategoriesRepo;
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseEntity>> getAllCourses() {
        List<CourseEntity> courses = coursesRepo.getAllCourses();
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }
}
