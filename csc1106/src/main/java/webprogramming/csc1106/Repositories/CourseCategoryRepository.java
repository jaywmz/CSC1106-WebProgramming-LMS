package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webprogramming.csc1106.Entities.CourseCategory;
import webprogramming.csc1106.Entities.UploadCourse;

import java.util.List;

@Repository
public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Long> {
    List<CourseCategory> findByCourse(UploadCourse course);
    CourseCategory findByCourseId(Long courseId);
    List<CourseCategory> findByCategoryGroupId(Long categoryGroupId);
    List<CourseCategory> findByCourseIdIn(List<Long> courseIds);
}
