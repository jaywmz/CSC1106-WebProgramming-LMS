package webprogramming.csc1106.Repositories;

// Import necessary packages and classes
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webprogramming.csc1106.Entities.UploadCourse;

import java.util.List;

public interface UploadCourseRepository extends JpaRepository<UploadCourse, Long> {

    // Find all UploadCourse entities by a specific category group ID
    List<UploadCourse> findByCourseCategories_CategoryGroup_Id(Long categoryId);

    // Find all UploadCourse entities where isApproved is false
    List<UploadCourse> findByIsApprovedFalse();

    // Find all UploadCourse entities where isApproved is true
    List<UploadCourse> findByIsApprovedTrue();

    // Find all approved UploadCourse entities by a specific category group ID
    List<UploadCourse> findByCourseCategories_CategoryGroup_IdAndIsApprovedTrue(Long categoryId);

    // Find all approved UploadCourse entities by a specific user ID
    List<UploadCourse> findByUser_UserIdAndIsApprovedTrue(Integer userId);

    // Find all UploadCourse entities by a specific user ID
    List<UploadCourse> findByUser_UserId(Integer userId);

    // Find a specific UploadCourse by its ID
    UploadCourse findById(int id);

    // Custom query to fetch UploadCourse with its details (sections, ratings, course categories)
    @Query("SELECT c FROM UploadCourse c " +
           "LEFT JOIN FETCH c.sections s " +
           "LEFT JOIN FETCH c.ratings r " +
           "LEFT JOIN FETCH c.courseCategories cc " +
           "WHERE c.id = :id")
    UploadCourse findByIdWithDetails(@Param("id") Long id);
}
