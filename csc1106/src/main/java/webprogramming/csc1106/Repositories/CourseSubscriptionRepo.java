package webprogramming.csc1106.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import webprogramming.csc1106.Entities.CourseSubscriptionEntity;

public interface CourseSubscriptionRepo extends JpaRepository<CourseSubscriptionEntity, String>{
    
    @Query("SELECT c FROM CourseSubscriptionEntity c WHERE c.id = :id")
    CourseSubscriptionEntity findByCourseSubscriptionId(@Param("id") int id);

    @Query("SELECT c FROM CourseSubscriptionEntity c WHERE c.user.userId = :userId")
    List<CourseSubscriptionEntity> findByUserId(@Param("userId") int userId);

    @Query("SELECT c FROM CourseSubscriptionEntity c WHERE c.course.id = :courseId")
    List<CourseSubscriptionEntity> findByCourseId(@Param("courseId") int courseId);

    @Query("SELECT c FROM CourseSubscriptionEntity c WHERE c.subscriptionStatus = :subscriptionStatus")
    List<CourseSubscriptionEntity> findBySubscriptionStatus(@Param("subscriptionStatus") String subscriptionStatus);

    @Query("SELECT c FROM CourseSubscriptionEntity c")
    List<CourseSubscriptionEntity> getAllCourseSubscriptions();
}
