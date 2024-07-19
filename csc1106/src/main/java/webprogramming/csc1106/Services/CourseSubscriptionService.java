package webprogramming.csc1106.Services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import webprogramming.csc1106.Entities.CourseCategory;
import webprogramming.csc1106.Entities.CourseSubscriptionEntity;
import webprogramming.csc1106.Entities.UploadCourse;
import webprogramming.csc1106.Repositories.CourseSubscriptionRepo;
import webprogramming.csc1106.Repositories.UploadCourseRepository;

@Service
public class CourseSubscriptionService {

    @Autowired
    private CourseSubscriptionRepo courseSubscriptionRepo;

    @Autowired
    private UploadCourseRepository UploadCourseRepository;



    public List<CourseSubscriptionEntity> getSubscriptionsByCourseIds(List<Long> courseIds) {
        return courseSubscriptionRepo.findByCourseIdIn(courseIds);
    }

    

    @Transactional
    public void addpartnerSubscription(UploadCourse course, int userId) {
        long courseId = course.getId();
        List<CourseCategory> courseCategories = course.getCourseCategories();

        // Count existing free-ongoing subscriptions for the user
        int currentSubscriptions = courseSubscriptionRepo.countByUserIdAndSubscriptionStatus(userId, "free-ongoing");
        
        // // Check if the user already has a free-ongoing subscription for the course and status must be free-ongoing
        CourseSubscriptionEntity existingSubscription = courseSubscriptionRepo.findByUserIdAndCourseIdAndSubscriptionStatus(userId, courseId, "free-ongoing");
        if (existingSubscription != null) {
            throw new RuntimeException("User already has a free-ongoing subscription for this course.");
        }//

        // Check if user has less than 3 subscriptions
        if (currentSubscriptions < 3) {
            CourseSubscriptionEntity courseSubscriptionEntity = new CourseSubscriptionEntity();
            courseSubscriptionEntity.setCourseId(courseId);
            courseSubscriptionEntity.setCourseName(course.getTitle());
            courseSubscriptionEntity.setCourseInstructor(course.getLecturer());
            courseSubscriptionEntity.setCourseCoverImageUrl(course.getCoverImageUrl());
            courseSubscriptionEntity.setCourseDescription(course.getDescription());
            courseSubscriptionEntity.setUserId(userId);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            courseSubscriptionEntity.setSubscriptionDate(timestamp);
            courseSubscriptionEntity.setSubscriptionStatus("free-ongoing");
            courseSubscriptionEntity.setCompletedDate(null);
            courseSubscriptionEntity.setRecentlyUpdated(timestamp);
            courseSubscriptionEntity.setCourseCategory(courseCategories.get(0).getCategoryGroup().getName());

            // Save the new subscription
            courseSubscriptionRepo.save(courseSubscriptionEntity);
        } else {
            // Throw an exception or handle the situation where the user cannot add more subscriptions
            throw new RuntimeException("User already has 3 or more free-ongoing subscriptions.");
        }
    }

    
//check partner subscription
public boolean checkPartnerSubscription(int userId) {
    // Count subscriptions with status 'free-ongoing' for the given userId
    int count = courseSubscriptionRepo.countByUserIdAndSubscriptionStatus(userId, "free-ongoing");
    
    // Return true if the count is more than or equal to 3
    return count >= 3;
}

public void checkPartnerUnsubscription(UploadCourse course, int userId) {
    // Check if the user has a subscription
    if (courseSubscriptionRepo.countByUserIdAndSubscriptionStatus(userId, "free-ongoing") >= 1) {
        // Get the subscription
        CourseSubscriptionEntity subscription = courseSubscriptionRepo.findByCourseIdAndUserId(course.getId(), userId);

        // Get the current timestamp
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        // Get the subscription date
        Timestamp subscriptionDate = subscription.getSubscriptionDate();

        // Calculate the difference in milliseconds
        long difference = currentTimestamp.getTime() - subscriptionDate.getTime();

        // Calculate the difference in days
        long differenceInDays = difference / (1000 * 60 * 60 * 24);

        // Check if the difference is at least 3 days
        if (differenceInDays >= 3) {
            // Update the subscription status to 'Expired'
            subscription.setSubscriptionStatus("Expired");
            courseSubscriptionRepo.save(subscription);
        } else {
            throw new RuntimeException("To unsubscribe, the subscription date must be at least 3 days before the current timestamp.");
        }
    } else {
        throw new RuntimeException("No active subscription found.");
    }
}



public List<CourseSubscriptionEntity> getSubscriptionsByUserId(Integer userID) {
    return courseSubscriptionRepo.findByUserId(userID);
}


 @Transactional
    public List<UploadCourse> getCoursesByUserId(int userId) {
        List<CourseSubscriptionEntity> subscriptions = courseSubscriptionRepo.findByUserId(userId);
        List<UploadCourse> courses = new ArrayList<>();

        for (CourseSubscriptionEntity subscription : subscriptions) {
            UploadCourse course = UploadCourseRepository.findById(subscription.getCourseId()).orElse(null);
            if (course != null) {
                courses.add(course);
            }
        }

        return courses;
    }


}


