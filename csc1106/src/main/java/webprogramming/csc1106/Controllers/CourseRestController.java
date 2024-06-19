package webprogramming.csc1106.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List; // Import the List class
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
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


    // ===========================================================//
    //        Connection to the Marketplace Team Work             //
    // ===========================================================//

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

    // Get the details of the course
    @GetMapping("/course/details/{id}")
    public ResponseEntity<UploadCourse> getCourseDetails(@PathVariable("id") int id) {
        // Get the course details by course id
        UploadCourse course = uploadCourseRepository.findById((long) id).orElse(null);

        if (course == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(course, HttpStatus.OK);
    }



    // ===========================================================//
    //               Course Subscription REST API                 //
    // ===========================================================//

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


    // ===========================================================//
    //                  Cart Items REST API                       //
    // ===========================================================//

    // Get all cart items by user id
    @GetMapping("/cartitems/user/{id}")
    public ResponseEntity<List<CartItemEntity>> getCartItemsByUserId(@PathVariable("id") int id) {
        List<CartItemEntity> cartItems = cartItemsRepo.getCartItemsByUserId(id);
        if (cartItems.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(cartItems, HttpStatus.OK);
    }
    
    // Delete Cart Item by cart id
    @GetMapping("/cartitems/delete/{id}")
    public ResponseEntity<String> deleteCartItem(@PathVariable("id") String id) {
        cartItemsRepo.deleteCartItemById(id);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }

    // Get User Balance
    @GetMapping("/users/balance/{id}")
    public ResponseEntity<BigDecimal> getUserBalance(@PathVariable("id") int id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(user.getUserBalance(), HttpStatus.OK);
    }

    // Update User Balance
    @GetMapping("/users/balance/deduct/{id}")
    public ResponseEntity<String> updateUserBalance(@PathVariable("id") int id, @RequestParam("balance") BigDecimal balance) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        BigDecimal currentBalance = user.getUserBalance();
        if (currentBalance.compareTo(balance) < 0) {
            return new ResponseEntity<>("Insufficient balance", HttpStatus.BAD_REQUEST);
        }
        balance = currentBalance.subtract(balance);
        user.setUserBalance(balance);
        userRepository.save(user);
        return new ResponseEntity<>("Updated", HttpStatus.OK);
    }

    // Add courseSubscription after payment
    @GetMapping("/coursesubscriptions/add/{userId}/{courseId}")
    public ResponseEntity<String> addCourseSubscription(@PathVariable("userId") int userId, @PathVariable("courseId") int courseId) {
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        UploadCourse course = uploadCourseRepository.findById((long) courseId).orElse(null);

        if (course == null) {
            return new ResponseEntity<>("Course not found", HttpStatus.NOT_FOUND);
        }

        CourseCategory category = courseCategoryRepository.findByCourseId((long) courseId);
        CourseSubscriptionEntity courseSub = new CourseSubscriptionEntity();
        courseSub.setUserId(userId);
        courseSub.setCourseId(courseId);
        courseSub.setCourseName(course.getTitle());
        courseSub.setCourseInstructor(course.getLecturer());
        courseSub.setCourseCoverImageUrl(course.getCoverImageUrl());
        courseSub.setCourseDescription(course.getDescription());
        courseSub.setRecentlyUpdated(null);
        courseSub.setCompletedDate(null);
        courseSub.setSubscriptionDate(Timestamp.from(Instant.now()));
        courseSub.setSubscriptionStatus("ongoing");
        courseSub.setCourseCategory(category.getCategoryGroup().getName());
        
        courseSubscriptionRepo.save(courseSub);

        return ResponseEntity.ok("Added");
    }



    // ===========================================================//
    //                  Add to Cart REST API                      //
    // ===========================================================//

    // Check the courseSubscription to see if the user had subscribed to the course
    @GetMapping("/coursesubscriptions/check/{userId}/{courseId}")
    public ResponseEntity<String> checkCourseSubscription(@PathVariable("userId") int userId, @PathVariable("courseId") int courseId) {
        CourseSubscriptionEntity courseSubscription = courseSubscriptionRepo.findByCourseIdAndUserId(courseId, userId);
        if (courseSubscription == null) {
            return new ResponseEntity<>("Not subscribed", HttpStatus.OK);
        }
        return new ResponseEntity<>("Subscribed", HttpStatus.OK);
    }

}   
