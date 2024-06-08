package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogramming.csc1106.Entities.UploadCourse;
import java.util.List;

public interface UploadCourseRepository extends JpaRepository<UploadCourse, Long> {
    List<UploadCourse> findByCourseCategories_CategoryGroup_Id(Long categoryId);
}
