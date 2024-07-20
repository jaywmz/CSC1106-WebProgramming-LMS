package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import webprogramming.csc1106.Entities.Rating;
import webprogramming.csc1106.Entities.UploadCourse;
import webprogramming.csc1106.Entities.User;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByCourseAndUser(UploadCourse course, User user);
    List<Rating> findByCourseId(Long courseId);
    List<Rating> findByCourse(UploadCourse course); // Add this line
}
