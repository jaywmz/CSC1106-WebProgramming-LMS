package webprogramming.csc1106.Repositories;

// Import necessary packages and classes
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webprogramming.csc1106.Entities.CourseCategory;
import webprogramming.csc1106.Entities.UploadCourse;

import java.util.List;

@Repository // Indicate that this interface is a Spring Data JPA repository
public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Long> {

    // Find all CourseCategory entities by a specific UploadCourse
    List<CourseCategory> findByCourse(UploadCourse course);

    // Find a CourseCategory entity by the course ID
    CourseCategory findByCourseId(Long courseId);

    // Find all CourseCategory entities by a specific category group ID
    List<CourseCategory> findByCategoryGroupId(Long categoryGroupId);

    // Find all CourseCategory entities where the course ID is in the given list
    List<CourseCategory> findByCourseIdIn(List<Long> courseIds);
}
