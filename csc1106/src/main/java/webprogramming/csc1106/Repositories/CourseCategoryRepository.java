package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogramming.csc1106.Entities.CourseCategory;

public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Long> {
}
