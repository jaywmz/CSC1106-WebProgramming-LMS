package webprogramming.csc1106.Repositories;

// Import necessary packages and classes
import org.springframework.data.jpa.repository.JpaRepository;
import webprogramming.csc1106.Entities.Rating;
import webprogramming.csc1106.Entities.UploadCourse;
import webprogramming.csc1106.Entities.User;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    // Find a Rating by course and user
    Optional<Rating> findByCourseAndUser(UploadCourse course, User user);

    // Find all Ratings by course ID
    List<Rating> findByCourseId(Long courseId);

    // Find all Ratings by course
    List<Rating> findByCourse(UploadCourse course);
}
