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
import webprogramming.csc1106.Services.CourseService;
import java.util.List; // Import the List class
import java.util.ArrayList;
import webprogramming.csc1106.Models.Course; // Import the Course class

@RestController
public class CourseController {
    private final CourseService courseService;
    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);
    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    public List<Course> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return courses;
    }

    @GetMapping("/course")
    public ResponseEntity<List<Course>> getCourse(@RequestParam(required = false) Integer courseId, 
                                                @RequestParam(required = false) String courseInstructor, 
                                                @RequestParam(required = false) String courseLevel) {
        List<Course> courses = new ArrayList<>();

        if (courseId != null) {
            Course course = courseService.getCourseById(courseId);
            if (course != null) {
                courses.add(course);
            }
        } else if (courseInstructor != null) {
            courses = courseService.getCoursesByInstructor(courseInstructor);
        } else if (courseLevel != null) {
            courses = courseService.getCoursesByLevel(courseLevel);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (courses.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(courses, HttpStatus.OK);
    }


}
