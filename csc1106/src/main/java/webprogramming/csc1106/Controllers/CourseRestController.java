package webprogramming.csc1106.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import webprogramming.csc1106.Entities.*;
import webprogramming.csc1106.Repositories.*;
import webprogramming.csc1106.Services.UploadCourseService;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class CourseRestController {
    private final UserRepository userRepository;
    private final CourseSubscriptionRepo courseSubscriptionRepo;
    private final CourseCategoryRepository courseCategoryRepository;
    private final CartItemsRepo cartItemsRepo;
    private final UploadCourseRepository uploadCourseRepository;
    private final RatingRepository ratingRepository;
    

    @Autowired
    public CourseRestController(UserRepository userRepository, CourseSubscriptionRepo courseSubscriptionRepo, 
                                CourseCategoryRepository courseCategoryRepository, CartItemsRepo cartItemsRepo, 
                                UploadCourseRepository uploadCourseRepository, RatingRepository ratingRepository) {
        this.courseSubscriptionRepo = courseSubscriptionRepo;
        this.courseCategoryRepository = courseCategoryRepository;
        this.cartItemsRepo = cartItemsRepo;
        this.uploadCourseRepository = uploadCourseRepository;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
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

    // Get the details of the course with calculated ratings
    @GetMapping("/course/details/{id}")
    public ResponseEntity<UploadCourse> getCourseDetails(@PathVariable("id") int id) {
        Optional<UploadCourse> courseOpt = uploadCourseService.getCourseById((long) id);
        if (courseOpt.isPresent()) {
            UploadCourse course = courseOpt.get();
            uploadCourseService.calculateRating(course);
            return new ResponseEntity<>(course, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
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

    // Getting section and lesson from their IDs
    @Autowired
    private UploadCourseService uploadCourseService;

    @GetMapping("/api/section/{sectionId}")
    public ResponseEntity<Section> getSectionDetails(@PathVariable Long sectionId) {
        Section section = uploadCourseService.getSectionById(sectionId);
        if (section == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(section);
    }

    @GetMapping("/api/section/{sectionId}/lessons")
    public ResponseEntity<List<Lesson>> getSectionLessons(@PathVariable Long sectionId) {
        List<Lesson> lessons = uploadCourseService.getLessonsBySectionId(sectionId);
        return ResponseEntity.ok(lessons);
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

    // Check if the course is already in cart
    @GetMapping("/cartitems/check/{userId}/{courseId}")
    public ResponseEntity<String> checkCartItem(@PathVariable("userId") int userId, @PathVariable("courseId") int courseId) {
        CartItemEntity cartItem = cartItemsRepo.getCartItemByUserIdAndCourseId(userId, courseId);
        if (cartItem == null) {
            return new ResponseEntity<>("Not in cart", HttpStatus.OK);
        }
        return new ResponseEntity<>("In cart", HttpStatus.OK);
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

    // Add to cart
    @GetMapping("/cartitems/add/{userId}/{courseId}")
    public ResponseEntity<String> addToCart(@PathVariable("userId") int userId, @PathVariable("courseId") int courseId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        UploadCourse course = uploadCourseRepository.findById((long) courseId).orElse(null);

        if (course == null) {
            return new ResponseEntity<>("Course not found", HttpStatus.NOT_FOUND);
        }

        CartItemEntity cartItem = new CartItemEntity();
        cartItem.setUserId(userId);
        cartItem.setCourseId(courseId);
        cartItem.setCourseName(course.getTitle());
        cartItem.setCourseInstructor(course.getLecturer());
        cartItem.setCourseCategory(courseCategoryRepository.findByCourseId((long) courseId).getCategoryGroup().getName());
        cartItem.setCoursePrice(BigDecimal.valueOf(course.getPrice()));
        cartItem.setAddedAt(Timestamp.from(Instant.now()));

        cartItemsRepo.save(cartItem);

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


    // ===========================================================//
    //                  Review REST API                           //
    // ===========================================================//
  
    // Add review for a course
    @PostMapping("/courses/{courseId}/review")
    public ResponseEntity<String> addReview(@PathVariable Long courseId,
                                            @RequestParam Integer userId,
                                            @RequestParam Double rating,
                                            @RequestParam String comment) {
        try {
            uploadCourseService.addReview(courseId, userId, rating, comment);
            return ResponseEntity.ok("Review submitted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to submit review.");
        }
    }
    // Add an endpoint to fetch reviews for a course
    @GetMapping("/courses/{courseId}/reviews")
@ResponseBody
public ResponseEntity<List<Map<String, Object>>> getReviews(@PathVariable Long courseId) {
    List<Rating> reviews = ratingRepository.findByCourseId(courseId);
    List<Map<String, Object>> reviewsWithUserNames = new ArrayList<>();

    for (Rating review : reviews) {
        Map<String, Object> reviewWithUserName = new HashMap<>();
        reviewWithUserName.put("score", review.getScore());
        reviewWithUserName.put("comment", review.getComment());
        reviewWithUserName.put("timestamp", review.getTimestamp());

        User user = review.getUser();
        if (user != null) {
            reviewWithUserName.put("userName", user.getUserName());
        } else {
            reviewWithUserName.put("userName", "Unknown");
        }

        reviewsWithUserNames.add(reviewWithUserName);
    }

    return ResponseEntity.ok(reviewsWithUserNames);
}

 
    

}