package webprogramming.csc1106.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import webprogramming.csc1106.Entities.CourseSubscriptionEntity;
import webprogramming.csc1106.Entities.User;

public interface CourseSubscriptionRepo extends JpaRepository<CourseSubscriptionEntity, String>{
    
    // GET by user
    List<CourseSubscriptionEntity> findByUserId(int userId);

    // GET by list of course
    @Query("SELECT c FROM CourseSubscriptionEntity c WHERE c.courseId IN :courseIds")
    List<CourseSubscriptionEntity> findByCourseIdIn(@Param("courseIds") List<Long> courseIds);


    // GET all
    @Query("SELECT c FROM CourseSubscriptionEntity c")
    List<CourseSubscriptionEntity> findAll();

    // Get by course id and user id
    @Query("SELECT c FROM CourseSubscriptionEntity c WHERE c.courseId = :courseId AND c.userId = :userId")
    CourseSubscriptionEntity findByCourseIdAndUserId(@Param("courseId") long courseId, @Param("userId") int userId);

    int countByUserIdAndSubscriptionStatus(int userId, String string);

    //findByUserIdAndCourseIdAndSubscriptionStatus
    CourseSubscriptionEntity findByUserIdAndCourseIdAndSubscriptionStatus(int userId, long courseId, String string);
}
