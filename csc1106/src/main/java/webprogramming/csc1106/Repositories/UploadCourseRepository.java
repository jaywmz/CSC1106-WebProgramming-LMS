package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import webprogramming.csc1106.Entities.UploadCourse;
import org.springframework.data.domain.Sort;
import java.util.List;

public interface UploadCourseRepository extends JpaRepository<UploadCourse, Long> {
    List<UploadCourse> findByCourseCategories_CategoryGroup_Id(Long categoryId, Sort sort);
    List<UploadCourse> findByIsApprovedFalse();
    List<UploadCourse> findByIsApprovedTrue();
    List<UploadCourse> findByCourseCategories_CategoryGroup_IdAndIsApprovedTrue(Long categoryId);

    // Get specific course by course id
    UploadCourse findById(int id);

    @Query("SELECT c FROM UploadCourse c " +
           "LEFT JOIN FETCH c.sections s " +
           "LEFT JOIN FETCH c.ratings r " +
           "LEFT JOIN FETCH c.courseCategories cc " +
           "WHERE c.id = :id")
    UploadCourse findByIdWithDetails(@Param("id") Long id);
}